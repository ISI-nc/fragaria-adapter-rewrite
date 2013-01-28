package nc.isi.fragaria_adapter_rewrite.entities.views;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;

public interface ViewGeneratorManager {

	public void generate(Class<? extends Entity> entityClass);

	public void generate(EntityMetadata entityMetadata);

}