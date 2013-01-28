package nc.isi.fragaria_adapter_rewrite.entities.views;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

public interface ViewConfigProvider {

	ViewConfig provide(Class<? extends Entity> entityClass,
			Class<? extends QueryView> view, ViewConfigBuilder viewConfigBuilder);

}
