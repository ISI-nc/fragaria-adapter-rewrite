package nc.isi.fragaria_adapter_rewrite.entities;

public interface EntityMetadataFactory {

	EntityMetadata create(Class<? extends Entity> entityClass);

}