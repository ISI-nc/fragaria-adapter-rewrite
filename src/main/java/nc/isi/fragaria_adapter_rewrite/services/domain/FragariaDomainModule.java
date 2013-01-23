package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Map;

import nc.isi.fragaria_adapter_rewrite.services.domain.DsLoader.MasterDsLoader;
import nc.isi.fragaria_adapter_rewrite.services.domain.DsLoader.MasterDsLoaderImpl;
import nc.isi.fragaria_adapter_rewrite.services.domain.DsLoader.SpecificDsLoader;
import nc.isi.fragaria_adapter_rewrite.services.domain.DsLoader.YamlDsLoader;
import nc.isi.fragaria_adapter_rewrite.services.domain.DsLoader.YamlSerializer;
import nc.isi.fragaria_adapter_rewrite.services.domain.jackson.JacksonModule;
import nc.isi.fragaria_adapter_rewrite.services.domain.session.QueryExecutorForCollection;
import nc.isi.fragaria_adapter_rewrite.services.domain.session.QueryExecutorForCollectionImpl;
import nc.isi.fragaria_adapter_rewrite.services.domain.session.SessionManager;
import nc.isi.fragaria_adapter_rewrite.services.domain.session.SessionManagerImpl;

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
