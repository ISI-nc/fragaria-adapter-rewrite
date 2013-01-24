package nc.isi.fragaria_adapter_rewrite.services;

import java.util.Map;

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
import nc.isi.fragaria_adapter_rewrite.ressources.ConnectionData;
import nc.isi.fragaria_adapter_rewrite.ressources.ConnectionDataBuilder;
import nc.isi.fragaria_adapter_rewrite.ressources.ConnectionDataBuilderImpl;
import nc.isi.fragaria_adapter_rewrite.ressources.DataSourceProvider;
import nc.isi.fragaria_adapter_rewrite.ressources.DataSourceProviderImpl;
import nc.isi.fragaria_adapter_rewrite.ressources.Datasource;
import nc.isi.fragaria_adapter_rewrite.ressources.MasterDsLoader;
import nc.isi.fragaria_adapter_rewrite.ressources.MasterDsLoaderImpl;
import nc.isi.fragaria_adapter_rewrite.ressources.ResourceFinder;
import nc.isi.fragaria_adapter_rewrite.ressources.ResourceFinderImpl;
import nc.isi.fragaria_adapter_rewrite.ressources.SpecificDsLoader;
import nc.isi.fragaria_adapter_rewrite.ressources.YamlDsLoader;
import nc.isi.fragaria_adapter_rewrite.ressources.YamlSerializer;
import nc.isi.fragaria_adapter_rewrite.utils.jackson.JacksonModule;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.collect.Maps;

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
	}

	public void contributeMasterDsLoader(
			Configuration<SpecificDsLoader> configuration,
			YamlDsLoader yamlDsLoader) {
		configuration.add(yamlDsLoader);
	}

	public void contributeResourceFinder(Configuration<String> configuration) {
		configuration.add("nc.isi.fragaria_adapter_rewrite");
	}

	public void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add("elasticsearch.cluster", "test");
		configuration.add("dstype.couchdb", "CouchDB");
		configuration.add("dstype.jdbc", "jdbc");
	}

	public void contributeDataSourceProvider(
			MappedConfiguration<String, Datasource> configuration,
			MasterDsLoader masterDsLoader) {
		Map<String, Datasource> map = masterDsLoader.getDs();
		for (String key : map.keySet())
			configuration.add(key, map.get(key));
	}

	public void contributeAdapterManager(
			MappedConfiguration<String, Adapter> configuration,
			CouchDbAdapter couchDbAdapter) {
		configuration.add("CouchDB", couchDbAdapter);
	}

	public static ConnectionDataBuilder buildConnectionDataBuilder() {
		Map<String, Class<? extends ConnectionData>> map = Maps.newHashMap();
		map.put("CouchDB", CouchdbConnectionData.class);
		return new ConnectionDataBuilderImpl(map);
	}

}
