package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.google.common.collect.Maps;

public class CouchDbAdapter implements Adapter {
	private final Map<URL, CouchDbInstance> instanceCache = Maps
			.newConcurrentMap();
	private final Map<String, Datasource> datasources = Maps.newConcurrentMap();
	private final Map<Datasource, CouchDbConnector> connectors = Maps
			.newConcurrentMap();
	private final CouchDbSerializer serializer;

	public CouchDbAdapter(Collection<Datasource> datasources,
			CouchDbSerializer serializer) {
		init(datasources);
		this.serializer = serializer;
	}

	private void init(Collection<Datasource> datasources) {
		for (Datasource datasource : datasources) {
			this.datasources.put(datasource.getKey(), datasource);
			CouchdbConnectionData couchdbConnectionData = CouchdbConnectionData.class
					.cast(datasource.getDsMetadata().getConnectionData());
			if (!instanceCache.containsKey(couchdbConnectionData.getUrl()))
				addInstance(couchdbConnectionData);
			connectors.put(datasource,
					new StdCouchDbConnector(couchdbConnectionData.getDbName(),
							instanceCache.get(couchdbConnectionData.getUrl())));

		}
	}

	protected void addInstance(CouchdbConnectionData couchdbConnectionData) {
		HttpClient httpClient = new StdHttpClient.Builder().url(
				couchdbConnectionData.getUrl()).build();
		instanceCache.put(couchdbConnectionData.getUrl(),
				new StdCouchDbInstance(httpClient));
	}

	public <T extends Entity> QueryResponse<T> executeQuery(Query<T> query) {
		return null;
	}

	public void post(Object... objects) {

	}

	public void post(Collection<Object> objects) {

	}

}
