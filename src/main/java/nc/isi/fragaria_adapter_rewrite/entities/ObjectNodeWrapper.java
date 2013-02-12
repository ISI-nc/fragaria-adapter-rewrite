package nc.isi.fragaria_adapter_rewrite.entities;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.dao.ByViewQuery;
import nc.isi.fragaria_adapter_rewrite.dao.IdQuery;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericEmbedingViews.Id;
import nc.isi.fragaria_adapter_rewrite.entities.views.View;
import nc.isi.fragaria_adapter_rewrite.enums.Completion;
import nc.isi.fragaria_adapter_rewrite.enums.State;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public abstract class ObjectNodeWrapper implements Entity {
	private static final Logger LOGGER = Logger
			.getLogger(ObjectNodeWrapper.class);
	private final ObjectMapper objectMapper = FragariaObjectMapper.INSTANCE
			.get();
	private final ObjectNode node;

	public ObjectNodeWrapper(ObjectNode objectNode) {
		this.node = objectNode;
	}

	public ObjectNodeWrapper() {
		this.node = objectMapper.createObjectNode();
	}

	protected <T> T resolve(Class<T> propertyType, String propertyName) {
		checkNotNull(propertyType);
		checkNotNull(propertyName);
		T result = null;
		if (node.has(metadata().getJsonPropertyName(propertyName))) {
			return resolveFromNode(propertyType, propertyName);
		} else {
			// Object is new
			if (propertyName.equals(Entity.ID)) {
				setCompletion(Completion.FULL);
			}
			if (getCompletion() == Completion.FULL) {
				return result;
			}
			complete();
			return resolve(propertyType, propertyName);
		}
	}

	protected <T> T resolveFromNode(Class<T> propertyType, String propertyName) {
		try {
			T o = objectMapper
					.treeToValue(
							node.get(this.metadata().getJsonPropertyName(
									propertyName)), propertyType);
			if (o instanceof Entity
					&& (((Entity) o).getSession() == null || ((Entity) o)
							.getState() == State.COMMITED)) {
				((Entity) o).attributeSession(getSession());
			}
			return o;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void complete() {
		LOGGER.debug(String
				.format("complete %s for id %s", getClass(), getId()));
		if (getState() != State.NEW) {
			completeFromDS();
		}
		this.setCompletion(Completion.FULL);
	}

	protected void completeFromDS() {
		Class<? extends Entity> entityClass = getClass();
		Entity fromDB = getSession().getUnique(
				new IdQuery<>(entityClass, getId()), false);
		LOGGER.debug(String.format("fromDB with session %s completion %s",
				getSession().getId(), fromDB.getCompletion()));
		EntityMetadata entityMetadata = metadata();
		for (String propertyName : entityMetadata.propertyNames()) {
			if (node.has(entityMetadata.getJsonPropertyName(propertyName))) {
				continue;
			}
			if (metadata().isNotEmbededList(propertyName)) {
				continue;
			}
			Class<?> propertyType = metadata().propertyType(propertyName);
			write(propertyName, fromDB.readProperty(propertyType, propertyName));
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> Collection<T> resolveCollection(Class<T> propertyType,
			String propertyName) {
		checkParametersNotNull(propertyType, propertyName);
		Collection<T> result = Lists.newArrayList();
		LOGGER.debug(String.format(
				"resolve collection for entity %s with property %s ", this,
				propertyName));
		if (node.has(this.metadata().getJsonPropertyName(propertyName))) {
			LOGGER.debug("is in node");
			ArrayNode arrayNode = (ArrayNode) node.get(this.metadata()
					.getJsonPropertyName(propertyName));
			try {
				for (JsonNode jsonNode : arrayNode) {
					T temp = objectMapper.treeToValue(jsonNode, propertyType);
					if (temp instanceof Entity) {
						((Entity) temp).attributeSession(getSession());
					}
					result.add(temp);
				}
				return result;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			if (metadata().isNotEmbededList(propertyName)) {
				Class<? extends Entity> propertyEntity = propertyType
						.asSubclass(Entity.class);
				result = (Collection<T>) getListByBackReference(propertyName,
						propertyEntity);
				writeProperty(propertyName, result);
				complete();
				return result;
			} else {
				if (this.getCompletion() == Completion.FULL) {
					return result;
				}
				complete();
			}
			return resolveCollection(propertyType, propertyName);

		}
	}

	protected <T extends Entity> Collection<T> getListByBackReference(
			String propertyName, Class<T> propertyEntity) {
		return getSession().get(
				new ByViewQuery<>(propertyEntity, metadata().getPartial(
						propertyName)).filterBy(
						metadata().getBackReference(propertyName), getId()));
	}

	protected void checkParametersNotNull(Object... objects) {
		for (Object o : objects) {
			checkNotNull(o);
		}
	}

	/**
	 * Ecrit la propriété en fonction de la vue définie dans embeded si vue il y
	 * a
	 */
	@SuppressWarnings("unchecked")
	protected Boolean write(String propertyName, Object value) {
		checkParametersNotNull(propertyName);
		if (metadata().isNotEmbededList(propertyName))
			return false;
		if (value != null) {
			Class<? extends View> view = metadata().getEmbeded(propertyName);
			if (isEntity(value)) {
				if (view == null)
					view = Id.class;
				Entity property = Entity.class.cast(value);
				node.put(metadata().getJsonPropertyName(propertyName),
						getJson(property, view));
				return true;
			}
			if (Collection.class.isAssignableFrom(value.getClass())) {
				Class<?> propertyType = metadata().propertyParameterClasses(
						propertyName)[0];
				if (isEntity(propertyType)) {
					Collection<? extends Entity> collection = Collection.class
							.cast(value);
					ArrayNode array = objectMapper.createArrayNode();
					for (Entity temp : collection) {
						array.add(getJson(temp, view));
					}
					node.put(metadata().getJsonPropertyName(propertyName),
							array);
					return true;
				}
			}
			node.put(metadata().getJsonPropertyName(propertyName),
					objectMapper.valueToTree(value));
		} else {
			node.remove(propertyName);
		}
		return true;
	}

	@Override
	public ObjectNode toJSON() {
		return toJSON(Completion.PARTIAL);
	}

	@Override
	public ObjectNode toJSON(Completion completion) {
		if (getSession() != null && completion == Completion.FULL) {
			for (String property : metadata().writablesPropertyNames()) {
				Object value = metadata().read(this, property);
				write(property, value);
			}
		}
		return node.deepCopy();
	}

	private ObjectNode getJson(Entity entity, Class<? extends View> view) {
		return view == null ? entity.toJSON() : entity.toJSON(view);
	}

	public ObjectNode toJSON(Class<? extends View> view) {
		checkParametersNotNull(view);
		ObjectNode copy = objectMapper.createObjectNode();
		for (String property : metadata().propertyNames(view)) {
			JsonNode value = objectMapper.valueToTree(metadata().read(this,
					property));
			copy.put(metadata().getJsonPropertyName(property), value);
		}
		return copy;
	}

	protected boolean isEntity(Object o) {
		return o != null && isEntity(o.getClass());
	}

	protected boolean isEntity(Class<?> cl) {
		return cl != null && Entity.class.isAssignableFrom(cl);
	}

}
