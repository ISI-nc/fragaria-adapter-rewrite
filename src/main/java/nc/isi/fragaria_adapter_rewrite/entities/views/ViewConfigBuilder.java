package nc.isi.fragaria_adapter_rewrite.entities.views;

import java.io.File;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

public interface ViewConfigBuilder {

	ViewConfig build(File file);

	ViewConfig buildDefault(Class<? extends Entity> entityClass,
			Class<? extends QueryView> view);

}
