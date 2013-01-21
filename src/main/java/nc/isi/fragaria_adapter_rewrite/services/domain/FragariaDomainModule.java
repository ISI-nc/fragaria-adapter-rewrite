package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Map;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Symbol;

import com.google.common.collect.Maps;

public class FragariaDomainModule {
	public void bind(ServiceBinder binder) {
		binder.bind(AdapterManager.class, AdapterManagerImpl.class);
		binder.bind(DataSourceProvider.class, DataSourceProviderImpl.class);
		binder.bind(CouchDbSerializer.class);
		binder.bind(ElasticSearchAdapter.class);
		binder.bind(CouchDbAdapter.class);
		binder.bind(EntityBuilder.class, EntityBuilderImpl.class);
		binder.bind(EntityMetadataFactory.class,
				EntityMetadataFactoryImpl.class);
		binder.bind(ObjectMapperProvider.class, ObjectMapperProviderImpl.class);
		binder.bind(ObjectResolver.class, ObjectResolverImpl.class);
	}

	public void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add("elasticsearch-cluster", "test");
		configuration.add("dstype-couchdb", "CouchDB");
		configuration.add("dstype-jdbc", "jdbc");
	}
	
	public void contributeCouchDbAdapter(Configuration<Datasource> configuration) {
	}

	public void contributeAdapterManager(
			MappedConfiguration<String, Adapter> configuration,
			@Symbol("{dstype-couchdb}") String dsTypeCouchdb,
			CouchDbAdapter couchDbAdapter) {
		configuration.add(dsTypeCouchdb, couchDbAdapter);
	}

	public ConnectionDataBuilder buildConnectionDataBuilder(
			@Symbol("{dstype-couchdb}") String dsTypeCouchDB,
			@Symbol("{dstype-jdbc}") String dsTypeJdbc) {
		Map<String, Class<? extends ConnectionData>> map = Maps.newHashMap();
		map.put(dsTypeCouchDB, CouchdbConnectionData.class);
		return new ConnectionDataBuilderImpl(map);
	}

}
