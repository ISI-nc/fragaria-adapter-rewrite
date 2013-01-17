package nc.isi.fragaria_adapter_rewrite.services.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface EntityBuilder {
	public <E extends Entity> E build(ObjectNode objectNode,
			Class<E> entityClass);

	public <E extends Entity> E build(Class<E> entityClass);

}
