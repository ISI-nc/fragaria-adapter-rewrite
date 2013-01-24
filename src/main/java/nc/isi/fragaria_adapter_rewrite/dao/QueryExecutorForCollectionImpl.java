package nc.isi.fragaria_adapter_rewrite.dao;

import static com.mysema.query.alias.Alias.$;
import static com.mysema.query.alias.Alias.alias;
import static com.mysema.query.collections.MiniApi.from;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

public class QueryExecutorForCollectionImpl implements
		QueryExecutorForCollection {

	@Override
	public <T extends Entity> T getUnique(
			AbstractQuery<T> query, Collection<T> coll) {
		if (coll == null)
			return null;
		T entity = alias(query.getResultType());
		return from($(entity), coll).where(query.getPredicate()).uniqueResult(
				$(entity));
	}
}
