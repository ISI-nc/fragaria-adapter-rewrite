package nc.isi.fragaria_adapter_rewrite.entities.views;

import java.util.Map;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadataFactory;
import nc.isi.fragaria_adapter_rewrite.resources.DataSourceProvider;
import nc.isi.fragaria_adapter_rewrite.resources.Datasource;

import org.apache.log4j.Logger;

public class ViewGeneratorManagerImpl implements ViewGeneratorManager {
	private static final Logger LOGGER = Logger
			.getLogger(ViewGeneratorManagerImpl.class);
	private final Map<String, ViewGenerator> dsTypeGenerator;
	private final EntityMetadataFactory entityMetadataFactory;
	private final DataSourceProvider dataSourceProvider;
	private final ViewConfigBuilderProvider viewConfigBuilderProvider;

	public ViewGeneratorManagerImpl(Map<String, ViewGenerator> dsTypeGenerator,
			EntityMetadataFactory entityMetadataFactory,
			DataSourceProvider dataSourceProvider,
			ViewConfigBuilderProvider viewConfigBuilderProvider) {
		this.dsTypeGenerator = dsTypeGenerator;
		this.entityMetadataFactory = entityMetadataFactory;
		this.dataSourceProvider = dataSourceProvider;
		this.viewConfigBuilderProvider = viewConfigBuilderProvider;
	}

	@Override
	public void generate(Class<? extends Entity> entityClass) {
		generate(entityMetadataFactory.create(entityClass));
	}

	@Override
	public void generate(EntityMetadata entityMetadata) {
		LOGGER.info(String.format("generating views for : "
				+ entityMetadata.getEntityClass()));
		Datasource datasource = dataSourceProvider.provide(entityMetadata
				.getDsKey());
		String dsType = datasource.getDsMetadata().getType();
		LOGGER.debug("dsType : " + dsType);
		dsTypeGenerator.get(dsType).generate(entityMetadata,
				viewConfigBuilderProvider.provide(dsType));
	}

}
