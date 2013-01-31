package nc.isi.fragaria_adapter_rewrite.entities;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.dao.ByViewQuery;
import nc.isi.fragaria_adapter_rewrite.dao.IdQuery;
import nc.isi.fragaria_adapter_rewrite.entities.views.View;
import nc.isi.fragaria_adapter_rewrite.enums.Completion;
import nc.isi.fragaria_adapter_rewrite.enums.State;
import nc.isi.fragaria_adapter_rewrite.services.ObjectMapperProvider;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class ObjectResolverImpl implements ObjectResolver {
	private static final Logger LOGGER = Logger
			.getLogger(ObjectResolverImpl.class);
	private final ObjectMapper objectMapper;

	public ObjectResolverImpl(ObjectMapperProvider objectMapperProvider) {
		this.objectMapper = objectMapperProvider.provide();
	}

	@Override
	public <T> T resolve(ObjectNode node, Class<T> propertyType,
			String propertyName, Entity entity) {
		checkNotNull(node);
		checkNotNull(propertyType);
		checkNotNull(propertyName);
		checkNotNull(entity);
		T result = null;
		if (node.has(entity.metadata().getJsonPropertyName(propertyName))) {
			try {
				return objectMapper.treeToValue(
						node.get(entity.metadata().getJsonPropertyName(
								propertyName)), propertyType);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			if (propertyName.equals(Entity.ID)) {
				entity.setCompletion(Completion.FULL);
				return result;
			}
			if (entity.getCompletion() == Completion.FULL) {
				return result;
			}
			complete(node, entity);
			return resolve(node, propertyType, propertyName, entity);
		}
	}

	private void complete(ObjectNode node, Entity entity) {
		if (entity.getState() != State.NEW) {
			completeFromDS(node, entity);
		}
		entity.setCompletion(Completion.FULL);
	}

	protected void completeFromDS(ObjectNode node, Entity entity) {
		Class<? extends Entity> entityClass = entity.getClass();
		if (entity.getId() == null)
			return;
		Entity fromDB = entity.getSession().getUnique(
				new IdQuery<>(entityClass, entity.getId()));
		EntityMetadata entityMetadata = entity.metadata();
		for (String propertyName : entityMetadata.propertyNames()) {
			if (node.has(entityMetadata.getJsonPropertyName(propertyName))) {
				continue;
			}
			if (entity.metadata().canWrite(propertyName)) {
				entity.metadata().write(entity, propertyName,
						entity.metadata().read(fromDB, propertyName));
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Collection<T> resolveCollection(ObjectNode node,
			Class<T> propertyType, String propertyName, Entity entity) {
		checkParametersNotNull(node, propertyType, propertyName, entity);
		Collection<T> result = Lists.newArrayList();
		LOGGER.debug(String.format(
				"resolve collection for entity %s with property %s ", entity,
				propertyName));
		if (node.has(entity.metadata().getJsonPropertyName(propertyName))) {
			LOGGER.debug("is in node");
			ArrayNode arrayNode = (ArrayNode) node.get(entity.metadata()
					.getJsonPropertyName(propertyName));
			try {
				for (JsonNode jsonNode : arrayNode) {
					result.add(objectMapper.treeToValue(jsonNode, propertyType));
				}
				return result;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			if (entity.getCompletion() != Completion.FULL) {
				LOGGER.debug("is incomplete");
				if (entity.metadata().getEmbeded(propertyName) != null
						|| !isEntity(propertyType)) {
					return result;
				}
				Class<? extends Entity> propertyEntity = propertyType
						.asSubclass(Entity.class);
				if (entity.metadata().canWrite(propertyName)) {
					result = (Collection<T>) getListByBackReference(
							propertyName, entity, propertyEntity);
					entity.metadata().write(entity, propertyName, result);
					return result;
				} else {
					throw new NoWriteMethodFoundException(entity, propertyName);
				}
			} else {
				if (entity.getCompletion() == Completion.FULL) {
					return result;
				}
				complete(node, entity);
			}
			return resolveCollection(node, propertyType, propertyName, entity);

		}
	}

	protected Collection<? extends Entity> getListByBackReference(
			String propertyName, Entity entity,
			Class<? extends Entity> propertyEntity) {
		return entity.getSession().get(
				new ByViewQuery<>(propertyEntity, entity.metadata().getPartial(
						propertyName)).filterBy(entity.metadata()
						.getBackReference(propertyName), entity.getId()));
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
	@Override
	public void write(ObjectNode node, String propertyName, Object value,
			Entity entity) {
		checkParametersNotNull(node, propertyName, entity);
		if (value != null) {
			Class<? extends View> view = entity.metadata().getEmbeded(
					propertyName);
			if (view == null)
				return;
			if (isEntity(value)) {
				Entity property = Entity.class.cast(value);
				node.put(entity.metadata().getJsonPropertyName(propertyName),
						getJson(property, view));
				return;
			}
			if (Collection.class.isAssignableFrom(value.getClass())) {
				Class<?> propertyType = entity.metadata()
						.propertyParameterClasses(propertyName)[0];
				if (isEntity(propertyType)) {
					Collection<? extends Entity> collection = Collection.class
							.cast(value);
					ArrayNode array = objectMapper.createArrayNode();
					for (Entity temp : collection) {
						array.add(getJson(temp, view));
					}
					node.put(entity.metadata()
							.getJsonPropertyName(propertyName), array);
					return;
				}
			}
		}
		node.put(entity.metadata().getJsonPropertyName(propertyName),
				objectMapper.valueToTree(value));
	}

	private ObjectNode getJson(Entity entity, Class<? extends View> view) {
		return view == null ? entity.toJSON() : entity.toJSON(view);
	}

	public ObjectNode clone(ObjectNode node, Class<? extends View> view,
			Entity entity) {
		checkParametersNotNull(node, view, entity);
		ObjectNode copy = objectMapper.createObjectNode();
		for (String property : entity.metadata().propertyNames(view)) {
			JsonNode value = objectMapper.valueToTree(entity.metadata().read(
					entity, property));
			copy.put(entity.metadata().getJsonPropertyName(property), value);
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
