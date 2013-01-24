package nc.isi.fragaria_adapter_rewrite.dao.adapters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.isi.fragaria_adapter_rewrite.dao.CollectionQueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.SearchQuery;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadataFactory;
import nc.isi.fragaria_adapter_rewrite.services.ObjectMapperProvider;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ElasticSearchAdapter {
	private final TransportClient transportClient;
	private final EntityMetadataFactory entityMetadataFactory;
	private final ObjectMapper objectMapper;
	private final EntityBuilder entityBuilder;

	public ElasticSearchAdapter(EntityMetadataFactory entityMetadataFactory,
			ObjectMapperProvider objectMapperProvider,
			EntityBuilder entityBuilder) {
		this.objectMapper = objectMapperProvider.provide();
		this.entityMetadataFactory = entityMetadataFactory;
		this.entityBuilder = entityBuilder;
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "test").build();
		this.transportClient = new TransportClient(settings);
	}

	private <T extends Entity> Collection<T> serialize(
			final SearchResponse searchResponse, final Class<T> entityClass) {

		List<T> list = new ArrayList<T>((int) searchResponse.hits().totalHits());
		for (SearchHit hit : searchResponse.hits()) {
			try {
				list.add(entityBuilder.build(ObjectNode.class.cast(objectMapper
						.readTree(hit.sourceAsString())), entityClass));

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return list;

	}

	public <T extends Entity> CollectionQueryResponse<T> executeQuery(
			final SearchQuery<T> searchQuery) {
		checkNotNull(searchQuery);
		return new CollectionQueryResponse<>(serialize(search(searchQuery),
				searchQuery.getResultType()));
	}

	private <T extends Entity> SearchResponse search(
			final SearchQuery<T> searchQuery) {
		EntityMetadata entityMetadata = entityMetadataFactory
				.create(searchQuery.getResultType());
		return transportClient.prepareSearch(entityMetadata.getDsKey())
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(searchQuery.getQueryBuilder()).execute().actionGet();
	}

	public <T extends Entity> CollectionQueryResponse<T> executeQuery(
			final QueryBuilder queryBuilder, final Class<T> resultType) {
		checkNotNull(queryBuilder);
		checkNotNull(resultType);
		return executeQuery(new SearchQuery<>(resultType, queryBuilder));
	}

}
