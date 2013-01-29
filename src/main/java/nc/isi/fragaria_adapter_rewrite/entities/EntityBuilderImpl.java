package nc.isi.fragaria_adapter_rewrite.entities;

import java.lang.reflect.InvocationTargetException;

import nc.isi.fragaria_adapter_rewrite.services.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EntityBuilderImpl implements EntityBuilder {
	private final EntityMetadataFactory entityMetadataFactory;
	private final ObjectResolver objectResolver;
	private final ObjectMapper objectMapper;
	private static final Class<?>[] genericParamClasses = { ObjectNode.class,
			ObjectResolver.class, EntityMetadataFactory.class };

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
		E entity = build(objectMapper.createObjectNode(), entityClass);
		return entity;
	}

	@Override
	public <E extends Entity> E build(Class<E> entityClass, Object... params) {
		E entity = build(objectMapper.createObjectNode(), entityClass, params);
		return entity;

	}

	@Override
	public <E extends Entity> E build(ObjectNode objectNode,
			Class<E> entityClass, Object... params) {
		Class<?>[] paramClasses = new Class[params.length
				+ genericParamClasses.length];
		Object[] trueParams = new Object[params.length
				+ genericParamClasses.length];
		int i = 0;
		trueParams[0] = objectNode;
		for (Class<?> genericParam : genericParamClasses) {
			if (i != 0)
				trueParams[i] = get(genericParam);
			paramClasses[i] = genericParam;
			i++;
		}
		for (Object specificParam : params) {
			paramClasses[i] = specificParam.getClass();
			trueParams[i] = specificParam;
			i++;
		}
		try {
			return entityClass.getConstructor(paramClasses).newInstance(
					trueParams);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}

	}

	private Object get(Class<?> genericParam) {
		if (ObjectResolver.class.isAssignableFrom(genericParam))
			return objectResolver;
		if (EntityMetadataFactory.class.isAssignableFrom(genericParam))
			return entityMetadataFactory;
		throw new IllegalArgumentException(genericParam.getName());
	}
}
