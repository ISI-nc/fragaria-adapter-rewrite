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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class ObjectResolverImpl implements ObjectResolver {
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
		if (node.has(entity.getMetadata().getJsonPropertyName(propertyName))) {
			try {
				return objectMapper.treeToValue(
						node.get(entity.getMetadata().getJsonPropertyName(
								propertyName)), propertyType);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
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
		Entity fromDB = entity.getSession().getUnique(
				new IdQuery<>(entityClass, entity.getId()));
		EntityMetadata entityMetadata = entity.getMetadata();
		for (String propertyName : entityMetadata.propertyNames()) {
			if (node.has(entityMetadata.getJsonPropertyName(propertyName))) {
				continue;
			}
			if (entity.getMetadata().canWrite(propertyName)) {
				entity.getMetadata().write(entity, propertyName,
						entity.getMetadata().read(fromDB, propertyName));
			}
		}
	}

	@Override
	public <T> Collection<T> resolveCollection(ObjectNode node,
			Class<T> propertyType, String propertyName, Entity entity) {
		checkParametersNotNull(node, propertyType, propertyName, entity);
		Collection<T> result = Lists.newArrayList();
		if (node.has(entity.getMetadata().getJsonPropertyName(propertyName))) {
			ArrayNode arrayNode = (ArrayNode) node.get(entity.getMetadata()
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
			if (entity.getCompletion() == Completion.FULL) {
				if (entity.getMetadata().getEmbeded(propertyName) != null
						|| !isEntity(propertyType)) {
					return result;
				}
				Class<? extends Entity> propertyEntity = propertyType
						.asSubclass(Entity.class);
				if (entity.getMetadata().canWrite(propertyName)) {
					entity.getMetadata().write(
							entity,
							propertyName,
							getListByBackReference(propertyName, entity,
									propertyEntity));
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
				new ByViewQuery<>(propertyEntity, entity.getMetadata()
						.getPartial(propertyName)).filterBy(entity
						.getMetadata().getBackReference(propertyName), entity
						.getId()));
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
		checkParametersNotNull(node, value, propertyName, entity);
		if (value != null) {
			if (isEntity(value)) {
				Class<? extends View> view = entity.getMetadata().getEmbeded(
						propertyName);
				Entity property = Entity.class.cast(value);
				node.put(
						entity.getMetadata().getJsonPropertyName(propertyName),
						getJson(property, view));
				return;
			}
			if (Collection.class.isAssignableFrom(value.getClass())) {
				Class<?> propertyType = entity.getMetadata()
						.propertyParameterClasses(propertyName)[0];
				if (isEntity(propertyType)) {
					Collection<? extends Entity> collection = Collection.class
							.cast(value);
					ArrayNode array = objectMapper.createArrayNode();
					for (Entity temp : collection) {
						array.add(getJson(temp, entity.getMetadata()
								.getEmbeded(propertyName)));
					}
					node.put(
							entity.getMetadata().getJsonPropertyName(
									propertyName), array);
					return;
				}
			}
		}
		node.put(entity.getMetadata().getJsonPropertyName(propertyName),
				objectMapper.valueToTree(value));
	}

	private ObjectNode getJson(Entity entity, Class<? extends View> view) {
		return view == null ? entity.toJSON() : entity.toJSON(view);
	}

	public ObjectNode clone(ObjectNode node, Class<? extends View> view,
			Entity entity) {
		checkParametersNotNull(node, view, entity);
		ObjectNode copy = objectMapper.createObjectNode();
		for (String property : entity.getMetadata().propertyNames(view)) {
			JsonNode value = objectMapper.valueToTree(entity.getMetadata()
					.read(entity, property));
			copy.put(entity.getMetadata().getJsonPropertyName(property), value);
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
