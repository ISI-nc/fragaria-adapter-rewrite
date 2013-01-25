package nc.isi.fragaria_adapter_rewrite.dao.adapters;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nc.isi.fragaria_adapter_rewrite.dao.ByViewQuery;
import nc.isi.fragaria_adapter_rewrite.dao.CollectionQueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.Query;
import nc.isi.fragaria_adapter_rewrite.dao.UniqueQueryResponse;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadataFactory;
import nc.isi.fragaria_adapter_rewrite.enums.Completion;
import nc.isi.fragaria_adapter_rewrite.enums.State;
import nc.isi.fragaria_adapter_rewrite.resources.DataSourceProvider;

import org.apache.log4j.Logger;

import com.google.common.collect.LinkedListMultimap;

public class AdapterManagerImpl implements AdapterManager {
	private final Map<String, Adapter> adapters;
	private final DataSourceProvider dataSourceProvider;
	private final EntityMetadataFactory entityMetadataFactory;
	private static final Logger LOGGER = Logger
			.getLogger(AdapterManagerImpl.class);

	public AdapterManagerImpl(DataSourceProvider dataSourceProvider,
			EntityMetadataFactory entityMetadataFactory,
			Map<String, Adapter> adapters) {
		this.adapters = adapters;
		this.dataSourceProvider = dataSourceProvider;
		this.entityMetadataFactory = entityMetadataFactory;
	}

	@Override
	public <T extends Entity> CollectionQueryResponse<T> executeQuery(
			Query<T> query) {
		LOGGER.info("executing collection query : " + query);
		EntityMetadata entityMetadata = entityMetadataFactory.create(query
				.getResultType());
		String dsType = getDsType(entityMetadata);
		CollectionQueryResponse<T> queryResponse = adapters.get(dsType)
				.executeQuery(query);
		CollectionQueryResponse<T> collectionQueryResponse = (CollectionQueryResponse<T>) queryResponse;
		for (T response : collectionQueryResponse.getResponse()) {
			init(response, query, entityMetadata);
		}
		return queryResponse;
	}

	@Override
	public <T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(
			Query<T> query) {
		LOGGER.info("executing unique query : " + query);
		EntityMetadata entityMetadata = entityMetadataFactory.create(query
				.getResultType());
		String dsType = getDsType(entityMetadata);
		UniqueQueryResponse<T> queryResponse = adapters.get(dsType)
				.executeUniqueQuery(query);
		init(queryResponse.getResponse(), query, entityMetadata);
		return queryResponse;
	}

	protected <T extends Entity> void init(T entity, Query<T> query,
			EntityMetadata entityMetadata) {
		if (query instanceof ByViewQuery) {
			ByViewQuery<?> vQuery = ByViewQuery.class.cast(query);
			if (vQuery.getView() == null) {
				entity.setCompletion(Completion.FULL);
				return;
			}
			if (entityMetadata.propertyNames(vQuery.getView()).containsAll(
					entityMetadata.propertyNames())) {
				entity.setCompletion(Completion.FULL);
				return;
			}
		}
		entity.setCompletion(Completion.PARTIAL);
		entity.setState(State.COMMITED);
	}

	@Override
	public void post(Entity... entities) {
		LOGGER.info("post : " + entities);
		LinkedList<Entity> list = new LinkedList<>();
		for (Entity entity : entities) {
			list.addLast(entity);
		}
		post(list);
	}

	@Override
	public void post(List<Entity> entities) {
		LOGGER.info("post : " + entities);
		LinkedListMultimap<Adapter, Entity> dispatch = LinkedListMultimap
				.create();
		for (Entity entity : entities) {
			dispatch.put(adapters.get(getDsType(entity)), entity);
		}
		for (Adapter adapter : dispatch.keySet()) {
			adapter.post(new LinkedList<>(dispatch.get(adapter)));
		}

	}

	protected String getDsType(Entity entity) {
		return getDsType(entity.getMetadata());
	}

	protected String getDsType(EntityMetadata entityMetadata) {
		return dataSourceProvider.provide(entityMetadata.getDsKey())
				.getDsMetadata().getType();
	}

}
