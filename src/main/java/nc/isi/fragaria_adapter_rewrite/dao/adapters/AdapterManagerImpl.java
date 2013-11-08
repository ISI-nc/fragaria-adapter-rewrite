package nc.isi.fragaria_adapter_rewrite.dao.adapters;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nc.isi.fragaria_adapter_rewrite.dao.ByViewQuery;
import nc.isi.fragaria_adapter_rewrite.dao.CollectionQueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.IdQuery;
import nc.isi.fragaria_adapter_rewrite.dao.Query;
import nc.isi.fragaria_adapter_rewrite.dao.UniqueQueryResponse;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericQueryViews.All;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewConfig;
import nc.isi.fragaria_adapter_rewrite.enums.Completion;
import nc.isi.fragaria_adapter_rewrite.enums.State;
import nc.isi.fragaria_adapter_rewrite.resources.DataSourceProvider;
import nc.isi.fragaria_adapter_rewrite.services.EntityMetadataProvider;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;

public class AdapterManagerImpl implements AdapterManager {
	private final Map<String, Adapter> adapters;
	private final DataSourceProvider dataSourceProvider;
	private final EntityMetadataProvider entityMetadataProvider;
	private static final Logger LOGGER = Logger
			.getLogger(AdapterManagerImpl.class);

	public AdapterManagerImpl(DataSourceProvider dataSourceProvider,
			EntityMetadataProvider entityMetadataProvider,
			Map<String, Adapter> adapters) {
		this.adapters = adapters;
		this.dataSourceProvider = dataSourceProvider;
		this.entityMetadataProvider = entityMetadataProvider;
	}

	private Adapter getAdapter(EntityMetadata entityMetadata) {
		String dsType = getDsType(entityMetadata);
		return adapters.get(dsType);

	}

	private Adapter getAdapter(Class<? extends Entity> entityClass) {
		EntityMetadata entityMetadata = entityMetadataProvider
				.provide(entityClass);
		return getAdapter(entityMetadata);
	}

	@Override
	public <T extends Entity> CollectionQueryResponse<T> executeQuery(
			Query<T> query) {
		LOGGER.info("executing collection query : " + query);
		EntityMetadata entityMetadata = entityMetadataProvider.provide(query
				.getResultType());
		CollectionQueryResponse<T> queryResponse = getAdapter(entityMetadata)
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
		EntityMetadata entityMetadata = entityMetadataProvider.provide(query
				.getResultType());
		UniqueQueryResponse<T> queryResponse = getAdapter(entityMetadata)
				.executeUniqueQuery(query);
		init(queryResponse.getResponse(), query, entityMetadata);
		return queryResponse;
	}

	protected <T extends Entity> void init(T entity, Query<T> query,
			EntityMetadata entityMetadata) {
		LOGGER.debug(String.format(
				"init for query %s idQuery? %s byViewQuery? %s", query,
				(query instanceof IdQuery), (query instanceof ByViewQuery)));
		if (entity == null)
			return;
		if (query instanceof ByViewQuery) {
			ByViewQuery<?> vQuery = ByViewQuery.class.cast(query);
			if (vQuery.getView() == null || vQuery.getView().equals(All.class)) {
				entity.setCompletion(Completion.FULL);
			}
			if (entityMetadata.propertyNames(vQuery.getView()).containsAll(
					entityMetadata.propertyNames())) {
				entity.setCompletion(Completion.FULL);
			}
		} else {
			if (query instanceof IdQuery) {
				entity.setCompletion(Completion.FULL);
			} else {
				entity.setCompletion(Completion.PARTIAL);
			}
		}
		LOGGER.debug(String.format("entity completion : %s",
				entity.getCompletion()));
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
		for (Entity entity : ImmutableList.copyOf(entities)) {
			entity.prepareForCommit();
			dispatch.put(getAdapter(entity.getClass()), entity);
		}
		for (Adapter adapter : dispatch.keySet()) {
			adapter.post(new LinkedList<>(dispatch.get(adapter)));
		}

	}

	protected String getDsType(Entity entity) {
		return getDsType(entity.metadata());
	}

	protected String getDsType(EntityMetadata entityMetadata) {
		return dataSourceProvider.provide(entityMetadata.getDsKey())
				.getDsMetadata().getType();
	}

	@Override
	public Boolean exist(ViewConfig viewConfig, EntityMetadata entityMetadata) {
		return getAdapter(entityMetadata).exist(viewConfig, entityMetadata);
	}

	@Override
	public void buildView(ViewConfig viewConfig, EntityMetadata entityMetadata) {
		getAdapter(entityMetadata).buildView(viewConfig, entityMetadata);
	}

	@Override
	public Boolean exist(ViewConfig viewConfig,
			Class<? extends Entity> entityClass) {
		return exist(viewConfig, entityMetadataProvider.provide(entityClass));
	}

	@Override
	public void buildView(ViewConfig viewConfig,
			Class<? extends Entity> entityClass) {
		buildView(viewConfig, entityMetadataProvider.provide(entityClass));
	}

}
