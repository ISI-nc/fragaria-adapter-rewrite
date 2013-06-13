package nc.isi.fragaria_adapter_rewrite.entities.views;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

public interface ViewConfigBuilder {

	ViewConfig buildDefault(Class<? extends Entity> entityClass,
			Class<? extends QueryView> view);

	ViewConfig build(String name, String fileName);

}
