package nc.isi.fragaria_adapter_rewrite.services.domain;

public interface EntityMetadataFactory {

	public EntityMetadata create(Class<? extends Entity> entityClass);

}