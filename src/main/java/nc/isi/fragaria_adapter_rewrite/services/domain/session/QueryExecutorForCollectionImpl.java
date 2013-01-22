package nc.isi.fragaria_adapter_rewrite.services.domain.session;

import static com.mysema.query.alias.Alias.$;
import static com.mysema.query.collections.MiniApi.from;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.services.domain.ByViewQuery;
import nc.isi.fragaria_adapter_rewrite.services.domain.Entity;
import nc.isi.fragaria_adapter_rewrite.services.domain.IdQuery;
import nc.isi.fragaria_adapter_rewrite.services.domain.Query;

import com.mysema.query.alias.Alias;

public class QueryExecutorForCollectionImpl implements
		QueryExecutorForCollection {

	@Override
	public <T extends Entity> T getUniqueObjectFromEntityCollFor(Query<T> query, Collection<T> coll) {
		if(coll!=null){
			if(query instanceof ByViewQuery)
				return getUniqueObjectByViewQuery((ByViewQuery<T>)query, coll);
			else if(query instanceof IdQuery)
				return getUniqueObjectByIdQuery((IdQuery<T>)query, coll);
		}
		return null;
	}
	
	public <T extends Entity> T getUniqueObjectByViewQuery(ByViewQuery<T> query,Collection<T> coll){
		T entity = Alias.alias(query.getResultType());
		return (T) from($(entity), coll).where(query.getPredicate()).uniqueResult($(entity));
	}
	public  <T extends Entity> T getUniqueObjectByIdQuery(IdQuery<T> query,Collection<T> coll){
		T entity = Alias.alias(query.getResultType());
		return (T) from($(entity), coll).where($(entity.getId()).eq(query.getId())).uniqueResult($(entity));	
	}

}
