package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.beust.jcommander.internal.Maps;

public class AdapterManagerImpl implements AdapterManager {
	private final Map<String, Adapter> adapters = Maps.newHashMap();
	private final DataSourceProvider dataSourceProvider;

	public AdapterManagerImpl(DataSourceProvider dataSourceProvider) {
		this.dataSourceProvider = dataSourceProvider;
	}

	public <T extends Entity> QueryResponse<T> executeQuery(Query<T> query) {
		EntityMetadata entityMetadata = new EntityMetadata(query.getType());
		String dsType = dataSourceProvider.provide(entityMetadata.getDsKey())
				.getDsMetadata().getType();
		QueryResponse<T> queryResponse = adapters.get(dsType).executeQuery(
				query);
		if (queryResponse instanceof CollectionQueryResponse) {
			CollectionQueryResponse<T> collectionQueryResponse = (CollectionQueryResponse<T>) queryResponse;
			for (T response : collectionQueryResponse.getResponse()) {
				init(response, query, entityMetadata);
			}
		}
		return queryResponse;
	}

	protected <T extends Entity> void init(T entity, Query<T> query,
			EntityMetadata entityMetadata) {
		if (query.getView() == null) {
			entity.setCompletion(Completion.FULL);
			return;
		}
		if (entityMetadata.propertyNames(query.getView()).containsAll(
				entityMetadata.propertyNames())) {
			entity.setCompletion(Completion.FULL);
			return;
		}
		entity.setCompletion(Completion.PARTIAL);
		entity.setState(State.COMMITED);
	}

	@Override
	public void post(Entity... entities) {
		post(Arrays.asList(entities));
	}

	@Override
	public void post(Collection<Entity> entities) {
		// filtre les objets par classes
		// filtre les classes par databaseType
		// envoie les objets par database à l'adapter

	}

}
