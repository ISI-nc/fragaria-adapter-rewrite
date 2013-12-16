package nc.isi.fragaria_adapter_rewrite.services;

import java.util.Map.Entry;

import nc.isi.fragaria_adapter_rewrite.dao.QueryExecutorForCollection;
import nc.isi.fragaria_adapter_rewrite.dao.QueryExecutorForCollectionImpl;
import nc.isi.fragaria_adapter_rewrite.dao.adapters.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.dao.adapters.AdapterManagerImpl;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilderImpl;
import nc.isi.fragaria_adapter_rewrite.entities.FragariaObjectMapperContributor;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewConfigBuilderProvider;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewConfigBuilderProviderImpl;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewConfigProvider;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewConfigProviderImpl;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewGenerator;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewGeneratorImpl;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewGeneratorManager;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewGeneratorManagerImpl;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewInitializer;
import nc.isi.fragaria_adapter_rewrite.resources.ConnectionDataBuilder;
import nc.isi.fragaria_adapter_rewrite.resources.ConnectionDataBuilderImpl;
import nc.isi.fragaria_adapter_rewrite.resources.DataSourceProvider;
import nc.isi.fragaria_adapter_rewrite.resources.DataSourceProviderImpl;
import nc.isi.fragaria_adapter_rewrite.resources.Datasource;
import nc.isi.fragaria_adapter_rewrite.resources.MasterDsLoader;
import nc.isi.fragaria_adapter_rewrite.resources.MasterDsLoaderImpl;
import nc.isi.fragaria_adapter_rewrite.utils.jackson.EntityBeanDeserializerModifier;
import nc.isi.fragaria_adapter_rewrite.utils.jackson.EntityJacksonModule;
import nc.isi.fragaria_adapter_rewrite.utils.jackson.JacksonModule;
import nc.isi.fragaria_reflection.services.FragariaReflectionModule;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Le module Tapestry pour gérer l'ioc
 * 
 * @author justin
 * 
 */
@SubModule({ JacksonModule.class, FragariaReflectionModule.class })
public class FragariaDomainModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(AdapterManager.class, AdapterManagerImpl.class);
		binder.bind(EntityBuilder.class, EntityBuilderImpl.class);
		binder.bind(ObjectMapperProvider.class, ObjectMapperProviderImpl.class);
		binder.bind(MasterDsLoader.class, MasterDsLoaderImpl.class);
		binder.bind(DataSourceProvider.class, DataSourceProviderImpl.class);
		binder.bind(QueryExecutorForCollection.class,
				QueryExecutorForCollectionImpl.class);
		binder.bind(ConnectionDataBuilder.class,
				ConnectionDataBuilderImpl.class);
		binder.bind(ViewInitializer.class);
		binder.bind(ViewConfigBuilderProvider.class,
				ViewConfigBuilderProviderImpl.class);
		binder.bind(ViewConfigProvider.class, ViewConfigProviderImpl.class);
		binder.bind(ViewGeneratorManager.class, ViewGeneratorManagerImpl.class);
		binder.bind(ViewGenerator.class, ViewGeneratorImpl.class);
		binder.bind(FragariaObjectMapperContributor.class);
		binder.bind(EntityMetadataProvider.class,
				EntityMetadataProviderImpl.class);
	}

	public void contributeReflectionProvider(Configuration<String> configuration) {
		configuration.add("nc.isi.fragaria_adapter_rewrite");
	}

	public void contributeScannerProvider(Configuration<Scanner> configuration) {
		configuration.add(new SubTypesScanner());
	}

	public void contributeDataSourceProvider(
			MappedConfiguration<String, Datasource> configuration,
			MasterDsLoader masterDsLoader) {
		for (Entry<String, Datasource> entry : masterDsLoader.getDs()
				.entrySet()) {
			configuration.add(entry.getKey(), entry.getValue());
		}
	}

	public void contributeFragariaObjectMapperContributor(
			Configuration<Module> configuration,
			EntityBeanDeserializerModifier entityBeanDeserializerModifier) {
		configuration.add(new EntityJacksonModule(
				entityBeanDeserializerModifier));
		configuration.add(new JodaModule());
	}

	@Startup
	public void initialize(ViewInitializer viewInitializer,
			FragariaObjectMapperContributor fragariaObjectMapperContributor) {
		// viewInitializer.initialize();
		fragariaObjectMapperContributor.initialize();
	}

}
