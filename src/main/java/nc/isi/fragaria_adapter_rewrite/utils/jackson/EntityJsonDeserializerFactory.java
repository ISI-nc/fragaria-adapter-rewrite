package nc.isi.fragaria_adapter_rewrite.utils.jackson;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

public interface EntityJsonDeserializerFactory {

	public <T extends Entity> EntityJsonDeserializer<T> create(
			Class<T> entityClass);

}