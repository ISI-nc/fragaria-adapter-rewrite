package nc.isi.fragaria_adapter_rewrite.utils.jackson;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;

public class EntityJsonDeserializerFactoryImpl implements
		EntityJsonDeserializerFactory {
	private final EntityBuilder entityBuilder;

	public EntityJsonDeserializerFactoryImpl(EntityBuilder entityBuilder) {
		this.entityBuilder = entityBuilder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nc.isi.fragaria_adapter_rewrite.utils.jackson.EntityJsonDeserializerFactory
	 * #create(java.lang.Class)
	 */
	@Override
	public <T extends Entity> EntityJsonDeserializer<T> create(
			Class<T> entityClass) {
		return new EntityJsonDeserializer<>(entityClass, entityBuilder);
	}

}
