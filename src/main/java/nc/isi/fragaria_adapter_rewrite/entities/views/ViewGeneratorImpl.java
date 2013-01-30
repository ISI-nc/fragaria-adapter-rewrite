package nc.isi.fragaria_adapter_rewrite.entities.views;

import nc.isi.fragaria_adapter_rewrite.dao.adapters.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericQueryViews.All;

import org.apache.log4j.Logger;

public class ViewGeneratorImpl implements ViewGenerator {
	private static final Logger LOGGER = Logger
			.getLogger(ViewGeneratorImpl.class);
	private final ViewConfigProvider viewConfigProvider;
	private final AdapterManager adapterManager;

	public ViewGeneratorImpl(ViewConfigProvider viewConfigProvider,
			AdapterManager adapterManager) {
		this.viewConfigProvider = viewConfigProvider;
		this.adapterManager = adapterManager;
	}

	@Override
	public void generate(EntityMetadata entityMetadata,
			ViewConfigBuilder viewConfigBuilder) {
		for (Class<? extends QueryView> view : entityMetadata
				.getViews(QueryView.class)) {
			LOGGER.info("build view : " + view.getSimpleName());
			build(entityMetadata, viewConfigBuilder, view);
		}
		build(entityMetadata, viewConfigBuilder, All.class);
	}

	protected void build(EntityMetadata entityMetadata,
			ViewConfigBuilder viewConfigBuilder, Class<? extends QueryView> view) {
		ViewConfig viewConfig = viewConfigProvider.provide(
				entityMetadata.getEntityClass(), view, viewConfigBuilder);
		if (exist(viewConfig, entityMetadata)) {
			LOGGER.info("view already exists");
			return;
		}
		build(viewConfig, entityMetadata);
		LOGGER.info("view created");
	}

	protected void build(ViewConfig viewConfig, EntityMetadata entityMetadata) {
		adapterManager.buildView(viewConfig, entityMetadata);
	}

	protected Boolean exist(ViewConfig viewConfig, EntityMetadata entityMetadata) {
		return adapterManager.exist(viewConfig, entityMetadata);
	}

}
