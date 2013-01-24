package nc.isi.fragaria_adapter_rewrite.dao;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

import org.elasticsearch.index.query.QueryBuilder;

public class SearchQuery<T extends Entity> implements Query<T> {
	private final Class<T> resultType;
	private final QueryBuilder queryBuilder;

	public SearchQuery(Class<T> resultType, QueryBuilder queryBuilder) {
		this.resultType = resultType;
		this.queryBuilder = queryBuilder;
	}

	@Override
	public Class<T> getResultType() {
		return resultType;
	}

	public QueryBuilder getQueryBuilder() {
		return queryBuilder;
	}

}
