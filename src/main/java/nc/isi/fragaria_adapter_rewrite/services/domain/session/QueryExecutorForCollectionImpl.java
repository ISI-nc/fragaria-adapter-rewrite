package nc.isi.fragaria_adapter_rewrite.services.domain.session;

import static com.mysema.query.alias.Alias.$;
import static com.mysema.query.collections.MiniApi.from;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.services.domain.AbstractQuery;
import nc.isi.fragaria_adapter_rewrite.services.domain.Entity;
import nc.isi.fragaria_adapter_rewrite.services.domain.Query;

import com.mysema.query.alias.Alias;

public class QueryExecutorForCollectionImpl implements
		QueryExecutorForCollection {

	@Override
	public <T extends Entity> T getUniqueObjectFromEntityCollFor(Query<T> query, Collection<T> coll) {
		if(coll!=null){
			if(query instanceof AbstractQuery){
				T entity = Alias.alias(query.getResultType());
				return (T) from($(entity), coll).where(((AbstractQuery<T>)query).getPredicate()).uniqueResult($(entity));
			}
			
		}
		return null;
	}

}
