package nc.isi.fragaria_adapter_rewrite.services.domain;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectResolverImpl implements ObjectResolver {
	private final ObjectMapper objectMapper;
	private static final Logger LOGGER = Logger
			.getLogger(ObjectResolverImpl.class);

	public ObjectResolverImpl(ObjectMapperProvider objectMapperProvider) {
		this.objectMapper = objectMapperProvider.provide();
	}

	@Override
	public <T> T resolve(ObjectNode node, Class<T> propertyType,
			String propertyName, Entity entity) {
		T result = null;
		if (node.has(entity.getMetadata().getJsonPropertyName(propertyName))) {
			try {
				if (isEntity(propertyType))
					return objectMapper
							.readerWithView(
									entity.getMetadata().getEmbeded(
											propertyName))
							.readValue(
									node.get(entity.getMetadata()
											.getJsonPropertyName(propertyName)));
				return objectMapper.treeToValue(
						node.get(entity.getMetadata().getJsonPropertyName(
								propertyName)), propertyType);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			if (entity.getCompletion() == Completion.FULL)
				return result;
			complete(node, entity);
			return entity.readProperty(propertyType, propertyName);
		}
	}

	private void complete(ObjectNode node, Entity entity) {
		if (entity.getState() != State.NEW) {
			completeWithDB(node, entity);
		}
		entity.setCompletion(Completion.FULL);
	}

	protected void completeWithDB(ObjectNode node, Entity entity) {
		Class<? extends Entity> entityClass = entity.getClass();
		Entity fromDB = entity.getSession().getUnique(
				new IdQuery<>(entityClass, entity.getId()));
		EntityMetadata entityMetadata = entity.getMetadata();
		for (String propertyName : entityMetadata.propertyNames()) {
			if (node.has(entityMetadata.getJsonPropertyName(propertyName)))
				continue;
			Class<?> propertyType = entityMetadata.propertyType(propertyName);
			Object propertyValue = (Collection.class
					.isAssignableFrom(propertyType)) ? fromDB.readCollection(
					entityMetadata.propertyParameterClasses(propertyName)[0],
					propertyName) : fromDB.readProperty(propertyType,
					propertyName);
			entity.writeProperty(propertyName, propertyValue);
		}
	}

	@Override
	public <T> Collection<T> resolveCollection(ObjectNode node,
			Class<T> propertyType, String propertyName, Entity entity) {
		Collection<T> result = null;
		if (node.has(entity.getMetadata().getJsonPropertyName(propertyName))) {
			result = Lists.newArrayList();
			ArrayNode arrayNode = (ArrayNode) node.get(entity.getMetadata()
					.getJsonPropertyName(propertyName));
			try {
				for (JsonNode jsonNode : arrayNode) {
					if (isEntity(propertyType)) {
						result.add(objectMapper.convertValue(
								objectMapper.readerWithView(
										entity.getMetadata().getEmbeded(
												propertyName)).readValue(
										jsonNode), propertyType));
					} else {
						result.add(objectMapper.treeToValue(jsonNode,
								propertyType));
					}
				}
				return result;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			if (entity.getCompletion() == Completion.FULL) {
				if (entity.getMetadata().getEmbeded(propertyName) != null
						|| !isEntity(propertyType))
					return result;
				Class<? extends Entity> propertyEntity = propertyType
						.asSubclass(Entity.class);
				entity.writeProperty(
						propertyName,
						entity.getSession()
								.get(new ByViewQuery<>(propertyEntity, entity
										.getMetadata().getPartial(propertyName))
										.filterBy(
												entity.getMetadata()
														.getBackReference(
																propertyName),
												entity.getId())));
			} else {
				complete(node, entity);
			}
			return entity.readCollection(propertyType, propertyName);
		}
	}

	@Override
	public void write(ObjectNode node, String propertyName, Object value,
			Entity entity) {
		if (value != null) {
			checkArgument(entity.getMetadata().propertyType(propertyName)
					.isAssignableFrom(value.getClass()));
			if (isEntity(value)) {
				try {
					node.put(
							entity.getMetadata().getJsonPropertyName(
									propertyName),
							objectMapper.writerWithView(
									entity.getMetadata().getEmbeded(
											propertyName)).writeValueAsString(
									value));
					return;
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}
			if (Collection.class.isAssignableFrom(value.getClass())) {
				Class<?>[] parameterClasses = entity.getMetadata()
						.propertyParameterClasses(propertyName);
				if (Entity.class.isAssignableFrom(parameterClasses[0])) {
					Class<? extends View> view = entity.getMetadata()
							.getEmbeded(propertyName);
					if (view == null)
						return;
					try {
						node.put(
								entity.getMetadata().getJsonPropertyName(
										propertyName), objectMapper
										.writerWithView(view)
										.writeValueAsString(value));
						return;
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		node.put(entity.getMetadata().getJsonPropertyName(propertyName),
				objectMapper.valueToTree(value));
	}

	protected boolean isEntity(Object o) {
		return isEntity(o.getClass());
	}

	protected boolean isEntity(Class<?> cl) {
		return Entity.class.isAssignableFrom(cl);
	}

}
