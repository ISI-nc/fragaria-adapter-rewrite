package nc.isi.fragaria_adapter_rewrite.entities.views;

import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericQueryViews.All;

public abstract class AbstractGenerator implements ViewGenerator {
	private final ViewConfigProvider viewConfigProvider;

	public AbstractGenerator(ViewConfigProvider viewConfigProvider) {
		this.viewConfigProvider = viewConfigProvider;
	}

	@Override
	public void generate(EntityMetadata entityMetadata,
			ViewConfigBuilder viewConfigBuilder) {
		for (Class<? extends QueryView> view : entityMetadata
				.getViews(QueryView.class)) {
			build(entityMetadata, viewConfigBuilder, view);
		}
		build(entityMetadata, viewConfigBuilder, All.class);
	}

	protected void build(EntityMetadata entityMetadata,
			ViewConfigBuilder viewConfigBuilder, Class<? extends QueryView> view) {
		ViewConfig viewConfig = viewConfigProvider.provide(
				entityMetadata.getEntityClass(), view, viewConfigBuilder);
		if (exist(viewConfig, entityMetadata)) {
			return;
		}
		build(viewConfig, entityMetadata);
	}

	protected abstract void build(ViewConfig viewConfig,
			EntityMetadata entityMetadata);

	protected abstract Boolean exist(ViewConfig viewConfig,
			EntityMetadata entityMetadata);

}
