package nc.isi.fragaria_adapter_rewrite.entities;

import java.lang.reflect.InvocationTargetException;

import nc.isi.fragaria_adapter_rewrite.services.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EntityBuilderImpl implements EntityBuilder {
	private final EntityMetadataFactory entityMetadataFactory;
	private final ObjectResolver objectResolver;
	private final ObjectMapper objectMapper;

	public EntityBuilderImpl(EntityMetadataFactory entityMetadataFactory,
			ObjectResolver objectResolver,
			ObjectMapperProvider objectMapperProvider) {
		this.entityMetadataFactory = entityMetadataFactory;
		this.objectResolver = objectResolver;
		this.objectMapper = objectMapperProvider.provide();
	}

	@Override
	public <E extends Entity> E build(ObjectNode objectNode,
			Class<E> entityClass) {
		try {
			return entityClass.getConstructor(ObjectNode.class,
					ObjectResolver.class, EntityMetadataFactory.class)
					.newInstance(objectNode, objectResolver,
							entityMetadataFactory);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <E extends Entity> E build(Class<E> entityClass) {
		return build(objectMapper.createObjectNode(), entityClass);
	}

}
