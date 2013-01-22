package nc.isi.fragaria_adapter_rewrite.services.domain.jackson;

import nc.isi.fragaria_adapter_rewrite.services.domain.Entity;
import nc.isi.fragaria_adapter_rewrite.services.domain.EntityBuilder;

public class EntityJsonDeserializerFactory {
	private final EntityBuilder entityBuilder;

	public EntityJsonDeserializerFactory(EntityBuilder entityBuilder) {
		this.entityBuilder = entityBuilder;
	}

	public <T extends Entity> EntityJsonDeserializer<T> create(
			Class<T> entityClass) {
		return new EntityJsonDeserializer<>(entityBuilder, entityClass);
	}

}
