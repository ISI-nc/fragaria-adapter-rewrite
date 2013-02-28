package nc.isi.fragaria_adapter_rewrite.dao;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

import org.elasticsearch.index.query.QueryBuilder;

public class SearchQuery<T extends Entity> implements Query<T> {
	private final Class<T> resultType;
	private final QueryBuilder queryBuilder;
	private final int limit;

	public SearchQuery(Class<T> resultType, QueryBuilder queryBuilder,int limit) {
		this.resultType = resultType;
		this.queryBuilder = queryBuilder;
		this.limit = limit;
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

}
