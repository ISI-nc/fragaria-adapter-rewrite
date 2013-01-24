package nc.isi.fragaria_adapter_rewrite.utils.jackson;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;

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
