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
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mysema.query.QueryException;

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
		ViewQuery viewQuery = new ViewQuery().designDocId(
				query.getType().getSimpleName()).viewName(
				query.hasView() ? query.getView().getSimpleName() : "all");
		return null;
	}

	public <T extends Entity> CollectionQueryResponse<T> executeQuery(
			ViewQuery viewQuery, Class<T> type) {
		EntityMetadata entityMetadata = entityMetadataFactory.create(type);
		ViewResult result = getConnector(entityMetadata).queryView(viewQuery);
		Collection<T> collection = Lists.newArrayList();
		for (Row row : result) {
			collection.add(serializer.deSerialize(
					ObjectNode.class.cast(row.getDocAsNode()), type));
		}
		return new CollectionQueryResponse<>(collection);
	}

	public <T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(
			String id, Class<T> type) {
		EntityMetadata entityMetadata = entityMetadataFactory.create(type);
		return new UniqueQueryResponse<T>(getConnector(entityMetadata).get(
				type, id));
	}

	public <T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(
			ViewQuery viewQuery, Class<T> type) {
		EntityMetadata entityMetadata = entityMetadataFactory.create(type);
		ViewResult result = getConnector(entityMetadata).queryView(viewQuery);
		if (result.getTotalRows() > 1)
			throw new QueryException("Unique query return multiple result : "
					+ result.getTotalRows());
		return new UniqueQueryResponse<T>(serializer.deSerialize(
				ObjectNode.class.cast(result.getRows().get(0)), type));
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
		LinkedList<Entity> filtered = cleanMultipleEntries(entities);
		Set<CouchDbConnector> connectorsToFlush = Sets.newHashSet();
		for (Entity entity : filtered) {
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

	private LinkedList<Entity> cleanMultipleEntries(LinkedList<Entity> entities) {
		LinkedList<Entity> filtered = new LinkedList<>();
		Multimap<State, Entity> dispatch = LinkedListMultimap.create();
		for (Entity entity : entities) {
			State state = entity.getState();
			if (!dispatch.containsValue(entity)) {
				dispatch.put(state, entity);
				continue;
			}
			State oldState = lookForEntityState(dispatch, entity);
			manage(dispatch, state, oldState, entity);
		}
		return filtered;
	}

	private void manage(Multimap<State, Entity> dispatch, State state,
			State oldState, Entity entity) {
		switch (state) {
		case MODIFIED:
			switch (oldState) {
			case NEW:
				dispatch.put(oldState, entity);
				break;
			case MODIFIED:
				dispatch.put(oldState, entity);
				break;
			default:
				commitError(entity, oldState, state);
			}
		case DELETED:
			switch (oldState) {
			case NEW:
				dispatch.remove(oldState, entity);
				break;
			case MODIFIED:
				dispatch.remove(oldState, entity);
				dispatch.put(state, entity);
				break;
			default:
				commitError(entity, oldState, state);
			}
		default:
			commitError(entity, oldState, state);
		}

	}

	private void commitError(Entity entity, State oldState, State state) {
		throw new RuntimeException(
				String.format(
						"Erreur sur l'état de l'objet %s, déjà enregistré avec l'état %s et demande à passer à %s ",
						entity, oldState, state));
	}

	private State lookForEntityState(Multimap<State, Entity> dispatch,
			Entity entity) {
		for (State state : dispatch.keySet()) {
			if (dispatch.get(state).contains(entity))
				return state;
		}
		return null;
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
