package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.lang.reflect.InvocationTargetException;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class EntityBuilderImpl implements EntityBuilder {
	private final EntityMetadataFactory entityMetadataFactory;
	private final ObjectResolver objectResolver;
	private final ObjectMapperProvider objectMapperProvider;

	public EntityBuilderImpl(EntityMetadataFactory entityMetadataFactory,
			ObjectResolver objectResolver,
			ObjectMapperProvider objectMapperProvider) {
		this.entityMetadataFactory = entityMetadataFactory;
		this.objectResolver = objectResolver;
		this.objectMapperProvider = objectMapperProvider;
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
		return build(new ObjectNode(objectMapperProvider.provide()
				.getNodeFactory()), entityClass);
	}

}
