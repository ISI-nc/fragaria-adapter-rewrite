package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectResolverImpl implements ObjectResolver {
	private final Session session;
	private final ObjectMapper objectMapper;
	private static final Logger LOGGER = Logger
			.getLogger(ObjectResolverImpl.class);

	public ObjectResolverImpl(SessionManager sessionManager,
			ObjectMapperProvider objectMapperProvider) {
		this.session = sessionManager.createSession();
		this.objectMapper = objectMapperProvider.provide();
	}

	@Override
	public <T> T resolve(ObjectNode node, Class<T> propertyType,
			String propertyName, Entity entity) {
		T result = null;
		if (node.has(propertyName)) {
			try {
				return objectMapper.treeToValue(node.get(propertyName),
						propertyType);
			} catch (JsonProcessingException e) {
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
		Class<? extends Entity> entityClass = entity.getClass();
		Entity fromDB = session.getUnique(new IdQuery<>(entityClass, entity
				.getId()));
		EntityMetadata entityMetadata = entity.getMetadata();
		for (String propertyName : entityMetadata.propertyNames()) {
			if (node.has(propertyName))
				continue;
			Class<?> propertyType = entityMetadata.propertyType(propertyName);
			Object propertyValue;
			try {
				propertyValue = (Collection.class
						.isAssignableFrom(propertyType)) ? fromDB
						.readCollection(Class.forName(entityMetadata
								.propertyParameterTypes(propertyName)[0]
								.toString()), propertyName) : fromDB
						.readProperty(propertyType, propertyName);
				entity.writeProperty(propertyName, propertyValue);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		entity.setCompletion(Completion.FULL);
	}

	@Override
	public <T> Collection<T> resolveCollection(ObjectNode node,
			Class<T> propertyType, String propertyName, Entity entity) {
		Collection<T> result = null;
		if (node.has(propertyName)) {
			result = Lists.newArrayList();
			ArrayNode arrayNode = (ArrayNode) node.get(propertyName);
			try {
				for (JsonNode jsonNode : arrayNode) {
					result.add(objectMapper.treeToValue(jsonNode, propertyType));
				}
				return result;
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		} else {
			if (entity.getCompletion() == Completion.FULL) {
				if (entity.getMetadata().getEmbeded(propertyName) != null)
					return result;
				Class<? extends Entity> propertyEntity = propertyType
						.asSubclass(Entity.class);
				entity.writeProperty(propertyName, session
						.get(new ByViewQuery<>(propertyEntity, entity
								.getMetadata().getPartial(propertyName))
								.filterBy(entity.getMetadata()
										.getBackReference(propertyName), entity
										.getId())));
			} else {
				complete(node, entity);
			}
			return entity.readCollection(propertyType, propertyName);
		}
	}

	@Override
	public void write(ObjectNode node, String propertyName, Object value,
			Class<? extends View> view) {
		try {
			node.put(propertyName, objectMapper.writerWithView(view)
					.writeValueAsString(value));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
