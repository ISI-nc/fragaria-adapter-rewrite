package nc.isi.fragaria_adapter_rewrite.services;

import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;

public interface EntityMetadataProvider {

	public EntityMetadata provide(Class<?> typeClass);
}
