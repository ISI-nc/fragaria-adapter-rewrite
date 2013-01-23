package nc.isi.fragaria_adapter_rewrite.services.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.mysema.query.alias.Alias.$;
import static com.mysema.query.alias.Alias.alias;
import static com.mysema.query.collections.MiniApi.from;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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

import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class CouchDbAdapter extends AbstractAdapter implements Adapter {
	private final DataSourceProvider dataSourceProvider;
	private final EntityMetadataFactory entityMetadataFactory;
	private final CouchDbSerializer serializer;
	private final ElasticSearchAdapter elasticSearchAdapter;
	private final ObjectMapperProvider objectMapperProvider;
	private final LoadingCache<URL, CouchDbInstance> instanceCache = CacheBuilder
			.newBuilder().maximumSize(10)
			.build(new CacheLoader<URL, CouchDbInstance>() {

				@Override
				public CouchDbInstance load(URL key) throws Exception {
					HttpClient httpClient = new StdHttpClient.Builder()
							.url(key).build();
					return new StdCouchDbInstance(httpClient);
				}

			});
	private final LoadingCache<Datasource, CouchDbConnector> connectors = CacheBuilder
			.newBuilder().maximumSize(40)
			.build(new CacheLoader<Datasource, CouchDbConnector>() {

				@Override
				public CouchDbConnector load(Datasource key) throws Exception {
					CouchdbConnectionData couchdbConnectionData = CouchdbConnectionData.class
							.cast(key.getDsMetadata().getConnectionData());
					return new StdCouchDbConnector(couchdbConnectionData
							.getDbName(), instanceCache
							.get(couchdbConnectionData.getUrl()),
							objectMapperProvider);
				}

			});

	public CouchDbAdapter(DataSourceProvider dataSourceProvider,
			CouchDbSerializer serializer,
			EntityMetadataFactory entityMetadataFactory,
			ElasticSearchAdapter elasticSearchAdapter,
			ObjectMapperProvider objectMapperProvider) {
		this.serializer = serializer;
		this.entityMetadataFactory = entityMetadataFactory;
		this.elasticSearchAdapter = elasticSearchAdapter;
		this.dataSourceProvider = dataSourceProvider;
		this.objectMapperProvider = objectMapperProvider;
	}

	public <T extends Entity> CollectionQueryResponse<T> executeQuery(
			final Query<T> query) {
		checkNotNull(query);
		if (query instanceof IdQuery)
			throw new IllegalArgumentException(
					"Impossible de renvoyer une Collection depuis une IdQuery");
		if (query instanceof ByViewQuery) {
			ByViewQuery<T> bVQuery = (ByViewQuery<T>) query;
			CollectionQueryResponse<T> response = executeQuery(
					buildViewQuery(bVQuery), bVQuery.getResultType());
			if (bVQuery.getPredicate() == null)
				return response;
			T entity = alias(query.getResultType());
			return buildQueryResponse(from($(entity), response.getResponse())
					.where(bVQuery.getPredicate()).list($(entity)));
		}
		if (query instanceof SearchQuery) {
			return elasticSearchAdapter.executeQuery((SearchQuery<T>) query);
		}
		throw new IllegalArgumentException(String.format(
				"Type de query inconnu : %s", query.getClass()));
	}

	protected <T extends Entity> ViewQuery buildViewQuery(ByViewQuery<T> bVQuery) {
		checkNotNull(bVQuery);
		ViewQuery vQuery = new ViewQuery().designDocId(
				buildDesignDocId(bVQuery)).viewName(
				bVQuery.getView().getSimpleName().toLowerCase());
		return bVQuery.getFilter().values().isEmpty() ? vQuery : vQuery
				.keys(bVQuery.getFilter().values());
	}

	protected <T extends Entity> String buildDesignDocId(ByViewQuery<T> bVQuery) {
		checkNotNull(bVQuery);
		return "_design/" + bVQuery.getResultType().getSimpleName();
	}

	public <T extends Entity> CollectionQueryResponse<T> executeQuery(
			ViewQuery viewQuery, Class<T> type) {
		checkNotNull(viewQuery);
		checkNotNull(type);
		EntityMetadata entityMetadata = entityMetadataFactory.create(type);
		ViewResult result = getConnector(entityMetadata).queryView(viewQuery);
		Collection<T> collection = Lists.newArrayList();
		for (Row row : result) {
			if (row.getValueAsNode() instanceof MissingNode)
				continue;
			collection.add(serializer.deSerialize(
					ObjectNode.class.cast(row.getValueAsNode()), type));
		}
		return buildQueryResponse(collection);
	}

	public <T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(
			String id, Class<T> type) {
		checkNotNull(id);
		checkNotNull(type);
		EntityMetadata entityMetadata = entityMetadataFactory.create(type);
		return buildQueryResponse(getConnector(entityMetadata).get(type, id));
	}

	@Override
	public <T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(
			Query<T> query) {
		checkNotNull(query);
		if (query instanceof IdQuery) {
			return executeUniqueQuery(((IdQuery<T>) query).getId(),
					query.getResultType());
		}
		CollectionQueryResponse<T> response = executeQuery(query);
		checkState(response.getResponse().size() == 1,
				"La requête a renvoyé trop de résultat");
		return buildQueryResponse(response.getResponse().iterator().next());
	}

	@Override
	public void post(Entity... entities) {
		LinkedList<Entity> list = new LinkedList<>();
		for (Entity entity : entities) {
			list.addLast(checkNotNull(entity));
		}
		post(list);
	}

	@Override
	public void post(LinkedList<Entity> entities) {
		checkNotNull(entities);
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
		for (Entity entity : filtered) {
			entity.setState(State.COMMITED);
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
		for (State state : dispatch.keySet()) {
			filtered.addAll(dispatch.get(state));
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

	private State lookForEntityState(Multimap<State, Entity> dispatch,
			Entity entity) {
		for (State state : dispatch.keySet()) {
			if (dispatch.get(state).contains(entity))
				return state;
		}
		return null;
	}

	protected CouchDbConnector getConnector(EntityMetadata entityMetadata) {
		checkNotNull(entityMetadata);
		Datasource ds = dataSourceProvider.provide(entityMetadata.getDsKey());
		CouchDbConnector couchDbConnector;
		try {
			couchDbConnector = connectors.get(ds);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		return couchDbConnector;
	}

	private Object deleteIfNeeded(Entity entity) {
		checkNotNull(entity);
		if (entity.getState() == State.DELETED)
			return new BulkDeleteDocument(entity.getId().toString(), entity
					.getRev().toString());
		return entity;
	}

}
