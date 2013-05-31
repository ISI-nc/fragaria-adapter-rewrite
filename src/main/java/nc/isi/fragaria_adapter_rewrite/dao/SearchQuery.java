package nc.isi.fragaria_adapter_rewrite.dao;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

import org.elasticsearch.index.query.QueryBuilder;

public class SearchQuery<T extends Entity> implements Query<T> {
	private final Class<T> resultType;
	private final QueryBuilder queryBuilder;
	private final int limit;
	private final int offset;
	private final boolean hasSortOrder;
	private ElasticSorting elasticSorting;

	public SearchQuery(Class<T> resultType, QueryBuilder queryBuilder,int limit) {
		this.resultType = resultType;
		this.queryBuilder = queryBuilder;
		this.limit = limit;
		this.offset=0;
		this.hasSortOrder = false;
	}
	
	public SearchQuery(Class<T> resultType, QueryBuilder queryBuilder,int limit,int offset) {
		this.resultType = resultType;
		this.queryBuilder = queryBuilder;
		this.limit = limit;
		this.offset=offset;
		this.hasSortOrder = false;
	}
	
	public SearchQuery(Class<T> resultType, QueryBuilder queryBuilder,int limit,int offset,ElasticSorting elasticSorting) {
		this.resultType = resultType;
		this.queryBuilder = queryBuilder;
		this.limit = limit;
		this.offset=offset;
		this.hasSortOrder = false;
		this.setElasticSorting(elasticSorting);
	}

	@Override
	public Class<T> getResultType() {
		return resultType;
	}

	public QueryBuilder getQueryBuilder() {
		return queryBuilder;
	}

	public int getLimit() {
		return limit;
	}

	public int getOffset() {
		return offset;
	}

	public boolean isHasSortOrder() {
		return hasSortOrder;
	}

	public ElasticSorting getElasticSorting() {
		return elasticSorting;
	}

	public void setElasticSorting(ElasticSorting elasticSorting) {
		this.elasticSorting = elasticSorting;
	}

}
