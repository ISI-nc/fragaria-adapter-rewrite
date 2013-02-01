package nc.isi.fragaria_adapter_rewrite.entities.views;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.resources.DataSourceProvider;
import nc.isi.fragaria_adapter_rewrite.resources.Datasource;

import org.apache.log4j.Logger;

public class ViewGeneratorManagerImpl implements ViewGeneratorManager {
	private static final Logger LOGGER = Logger
			.getLogger(ViewGeneratorManagerImpl.class);
	private final ViewGenerator viewGenerator;
	private final DataSourceProvider dataSourceProvider;
	private final ViewConfigBuilderProvider viewConfigBuilderProvider;

	public ViewGeneratorManagerImpl(ViewGenerator viewGenerator,
			DataSourceProvider dataSourceProvider,
			ViewConfigBuilderProvider viewConfigBuilderProvider) {
		this.viewGenerator = viewGenerator;
		this.dataSourceProvider = dataSourceProvider;
		this.viewConfigBuilderProvider = viewConfigBuilderProvider;
	}

	@Override
	public void generate(Class<? extends Entity> entityClass) {
		generate(new EntityMetadata(entityClass));
	}

	@Override
	public void generate(EntityMetadata entityMetadata) {
		LOGGER.info(String.format("generating views for : "
				+ entityMetadata.getEntityClass()));
		Datasource datasource = dataSourceProvider.provide(entityMetadata
				.getDsKey());
		String dsType = datasource.getDsMetadata().getType();
		LOGGER.debug("dsType : " + dsType);
		viewGenerator.generate(entityMetadata,
				viewConfigBuilderProvider.provide(dsType));
	}

}
