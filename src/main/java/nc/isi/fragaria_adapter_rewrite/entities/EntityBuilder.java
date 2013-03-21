package nc.isi.fragaria_adapter_rewrite.entities;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface EntityBuilder {
	<E extends Entity> E build(ObjectNode objectNode);

	<E extends Entity> E build(ObjectNode objectNode, Class<E> entityClass);

	<E extends Entity> E build(Class<E> entityClass);

	<E extends Entity> E build(Class<E> entityClass, Object... params);

	<E extends Entity> E build(ObjectNode objectNode, Class<E> entityClass,
			Object... params);

}
