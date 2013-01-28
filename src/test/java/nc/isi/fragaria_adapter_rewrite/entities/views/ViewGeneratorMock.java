package nc.isi.fragaria_adapter_rewrite.entities.views;

import nc.isi.fragaria_adapter_rewrite.dao.adapters.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;

public class ViewGeneratorMock extends ViewGeneratorImpl {

	public ViewGeneratorMock(ViewConfigProvider viewConfigProvider,
			AdapterManager adapterManager) {
		super(viewConfigProvider, adapterManager);
	}

	@Override
	protected void build(ViewConfig viewConfig, EntityMetadata entityMetadata) {
		if (viewConfig instanceof ViewConfigMock) {
			System.out.println("viewConfig : "
					+ ((ViewConfigMock) viewConfig).getContent());
		} else {
			System.out.println("error");
		}
	}

	@Override
	protected Boolean exist(ViewConfig viewConfig, EntityMetadata entityMetadata) {
		return false;
	}

}
