package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CouchDbAdapter implements Adapter {
	private final Map<URL, CouchDbInstance> instanceCache = Maps
			.newConcurrentMap();
	private final Map<String, Datasource> datasources = Maps.newConcurrentMap();
	private final Map<Datasource, CouchDbConnector> connectors = Maps
			.newConcurrentMap();
	private final EntityMetadataFactory entityMetadataFactory;
	private final CouchDbSerializer serializer;

	public CouchDbAdapter(Collection<Datasource> datasources,
			CouchDbSerializer serializer,
			EntityMetadataFactory entityMetadataFactory) {
		init(datasources);
		this.serializer = serializer;
		this.entityMetadataFactory = entityMetadataFactory;
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

	public <T extends Entity> CollectionQueryResponse<T> executeQuery(
			Query<T> query) {
		EntityMetadata entityMetadata = entityMetadataFactory.create(query
				.getType());
		CouchDbConnector connector = getConnector(entityMetadata);
		return null;
	}

	private <T extends Entity> CollectionQueryResponse<T> executeQuery(
			CouchDbConnector connector, ViewQuery viewQuery, Class<T> type) {
		ViewResult result = connector.queryView(viewQuery);
		Collection<T> collection = Lists.newArrayList();
		for (Row row : result) {
			collection.add(serializer.deSerialize(
					ObjectNode.class.cast(row.getDocAsNode()), type));
		}
		return new CollectionQueryResponse<>(collection);
	}

	@Override
	public <T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(
			Query<T> query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void post(Entity... entities) {
		LinkedList<Entity> list = new LinkedList<>();
		for (Entity entity : entities) {
			list.addLast(entity);
		}
		post(list);
	}

	@Override
	public void post(LinkedList<Entity> entities) {
		Set<CouchDbConnector> connectorsToFlush = Sets.newHashSet();
		for (Entity entity : entities) {
			CouchDbConnector couchDbConnector = getConnector(entity
					.getMetadata());
			if (!connectorsToFlush.contains(couchDbConnector))
				connectorsToFlush.add(couchDbConnector);
			couchDbConnector.addToBulkBuffer(deleteIfNeeded(entity));
		}
		for (CouchDbConnector connector : connectorsToFlush) {
			connector.flushBulkBuffer();
		}
	}

	protected CouchDbConnector getConnector(EntityMetadata entityMetadata) {
		Datasource ds = datasources.get(entityMetadata.getDsKey());
		CouchDbConnector couchDbConnector = connectors.get(ds);
		return couchDbConnector;
	}

	private Object deleteIfNeeded(Entity entity) {
		if (entity.getState() == State.DELETED)
			return new BulkDeleteDocument(entity.getId().toString(), entity
					.getRev().toString());
		return entity;
	}

}
