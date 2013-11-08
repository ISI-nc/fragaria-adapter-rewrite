package nc.isi.fragaria_adapter_rewrite.services;

import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_reflection.services.ObjectMetadataProvider;

public interface EntityMetadataProvider extends ObjectMetadataProvider {
	@Override
	public EntityMetadata provide(Class<?> typeClass);
}
