package nc.isi.fragaria_adapter_rewrite.utils.jackson;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

public class EntityJsonDeserializerFactoryImpl implements
		EntityJsonDeserializerFactory {

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
		return new EntityJsonDeserializer<>(entityClass);
	}

}
