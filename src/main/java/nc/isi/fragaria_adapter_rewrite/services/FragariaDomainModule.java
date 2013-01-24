package nc.isi.fragaria_adapter_rewrite.services;

import java.util.Map.Entry;

import nc.isi.fragaria_adapter_rewrite.couchdb.CouchDbAdapter;
import nc.isi.fragaria_adapter_rewrite.couchdb.CouchDbSerializer;
import nc.isi.fragaria_adapter_rewrite.couchdb.CouchdbConnectionData;
import nc.isi.fragaria_adapter_rewrite.dao.QueryExecutorForCollection;
import nc.isi.fragaria_adapter_rewrite.dao.QueryExecutorForCollectionImpl;
import nc.isi.fragaria_adapter_rewrite.dao.SessionManager;
import nc.isi.fragaria_adapter_rewrite.dao.SessionManagerImpl;
import nc.isi.fragaria_adapter_rewrite.dao.adapters.Adapter;
import nc.isi.fragaria_adapter_rewrite.dao.adapters.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.dao.adapters.AdapterManagerImpl;
import nc.isi.fragaria_adapter_rewrite.dao.adapters.ElasticSearchAdapter;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilderImpl;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadataFactory;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadataFactoryImpl;
import nc.isi.fragaria_adapter_rewrite.entities.ObjectResolver;
import nc.isi.fragaria_adapter_rewrite.entities.ObjectResolverImpl;
import nc.isi.fragaria_adapter_rewrite.resources.ConnectionDataBuilder;
import nc.isi.fragaria_adapter_rewrite.resources.ConnectionDataBuilderImpl;
import nc.isi.fragaria_adapter_rewrite.resources.DataSourceProvider;
import nc.isi.fragaria_adapter_rewrite.resources.DataSourceProviderImpl;
import nc.isi.fragaria_adapter_rewrite.resources.Datasource;
import nc.isi.fragaria_adapter_rewrite.resources.MasterDsLoader;
import nc.isi.fragaria_adapter_rewrite.resources.MasterDsLoaderImpl;
import nc.isi.fragaria_adapter_rewrite.resources.ResourceFinder;
import nc.isi.fragaria_adapter_rewrite.resources.ResourceFinderImpl;
import nc.isi.fragaria_adapter_rewrite.resources.SpecificDsLoader;
import nc.isi.fragaria_adapter_rewrite.resources.yaml.YamlDsLoader;
import nc.isi.fragaria_adapter_rewrite.resources.yaml.YamlSerializer;
import nc.isi.fragaria_adapter_rewrite.utils.jackson.JacksonModule;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.slf4j.bridge.SLF4JBridgeHandler;

@SubModule(JacksonModule.class)
public class FragariaDomainModule {
	static {
		SLF4JBridgeHandler.install();
	}

	public static void bind(ServiceBinder binder) {
		binder.bind(AdapterManager.class, AdapterManagerImpl.class);
		binder.bind(CouchDbSerializer.class);
		binder.bind(ElasticSearchAdapter.class);
		binder.bind(CouchDbAdapter.class);
		binder.bind(EntityBuilder.class, EntityBuilderImpl.class);
		binder.bind(EntityMetadataFactory.class,
				EntityMetadataFactoryImpl.class);
		binder.bind(ObjectMapperProvider.class, ObjectMapperProviderImpl.class);
		binder.bind(ObjectResolver.class, ObjectResolverImpl.class);
		binder.bind(MasterDsLoader.class, MasterDsLoaderImpl.class);
		binder.bind(ResourceFinder.class, ResourceFinderImpl.class);
		binder.bind(YamlDsLoader.class);
		binder.bind(YamlSerializer.class);
		binder.bind(DataSourceProvider.class, DataSourceProviderImpl.class);
		binder.bind(ReflectionFactory.class);
		binder.bind(SessionManager.class, SessionManagerImpl.class);
		binder.bind(QueryExecutorForCollection.class,
				QueryExecutorForCollectionImpl.class);
		binder.bind(ConnectionDataBuilder.class,
				ConnectionDataBuilderImpl.class);
	}

	public void contributeMasterDsLoader(
			Configuration<SpecificDsLoader> configuration,
			YamlDsLoader yamlDsLoader) {
		configuration.add(yamlDsLoader);
	}

	public void contributeResourceFinder(Configuration<String> configuration) {
		configuration.add("nc.isi.fragaria_adapter_rewrite");
	}

	public void contributeDataSourceProvider(
			MappedConfiguration<String, Datasource> configuration,
			MasterDsLoader masterDsLoader) {
		for (Entry<String, Datasource> entry : masterDsLoader.getDs()
				.entrySet())
			configuration.add(entry.getKey(), entry.getValue());
	}

	public void contributeAdapterManager(
			MappedConfiguration<String, Adapter> configuration,
			CouchDbAdapter couchDbAdapter) {
		configuration.add("CouchDB", couchDbAdapter);
	}

	public void contributeConnectionDataBuilder(
			MappedConfiguration<String, String> configuration) {
		configuration.add("CouchDB", CouchdbConnectionData.class.getName());
	}

}
