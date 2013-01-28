package nc.isi.fragaria_adapter_rewrite.entities.views;

import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;

public interface ViewGenerator {

	void generate(EntityMetadata entityMetadata,
			ViewConfigBuilder viewConfigBuilder);

}