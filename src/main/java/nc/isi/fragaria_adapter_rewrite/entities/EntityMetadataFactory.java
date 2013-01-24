package nc.isi.fragaria_adapter_rewrite.entities;

public interface EntityMetadataFactory {

	public EntityMetadata create(Class<? extends Entity> entityClass);

}