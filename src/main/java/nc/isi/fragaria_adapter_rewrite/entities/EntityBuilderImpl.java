package nc.isi.fragaria_adapter_rewrite.entities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;

public class EntityBuilderImpl implements EntityBuilder {
	private static final Logger LOGGER = Logger
			.getLogger(EntityBuilderImpl.class);

	@Override
	public <E extends Entity> E build(ObjectNode objectNode,
			Class<E> entityClass) {
		if (Modifier.isAbstract(entityClass.getModifiers())) {
			return build(objectNode);
		}
		LOGGER.info("building " + entityClass);
		try {
			return entityClass.getConstructor(ObjectNode.class).newInstance(
					objectNode);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <E extends Entity> E build(Class<E> entityClass) {
		try {
			return entityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public <E extends Entity> E build(Class<E> entityClass, Object... params) {
		E entity = build(null, entityClass, params);
		return entity;

	}

	@Override
	public <E extends Entity> E build(ObjectNode objectNode,
			Class<E> entityClass, Object... params) {
		int offset = objectNode != null ? 1 : 0;
		Class<?>[] paramClasses = new Class[params.length + offset];
		Object[] trueParams = new Object[params.length + offset];
		if (objectNode != null) {
			trueParams[0] = objectNode;
			paramClasses[0] = ObjectNode.class;
		}
		int i = offset;
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

	@Override
	public <E extends Entity> E build(ObjectNode objectNode) {
		try {
			Class<E> entityClass = FragariaObjectMapper.INSTANCE
					.getEntityClass(objectNode);
			if (Modifier.isAbstract(entityClass.getModifiers())) {
				throw new InstantiationException();
			}
			return build(objectNode, entityClass);
		} catch (JsonProcessingException | ClassNotFoundException
				| InstantiationException e) {
			throw Throwables.propagate(e);
		}
	}
}
