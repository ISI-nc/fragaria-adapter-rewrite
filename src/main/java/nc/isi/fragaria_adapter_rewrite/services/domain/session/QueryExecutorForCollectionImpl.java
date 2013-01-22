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
		if(query instanceof ByViewQuery)
			return getUniqueObjectFromEntityCollFor((ByViewQuery<T>)query, coll);
		else if(query instanceof IdQuery)
			return getUniqueObjectFromEntityCollFor((IdQuery<T>)query, coll);
		return null;
	}
	
	public <T extends Entity> T getUniqueObjectFromCollFor(ByViewQuery<T> query,Collection<T> coll){
		T alias = Alias.alias(query.getResultType());
		return (T) from($(alias), coll).where(query.getPredicate());	
	}
	public  <T extends Entity> T getUniqueObjectFromCollFor(IdQuery<T> query,Collection<T> coll){
		T alias = Alias.alias(query.getResultType());
		return (T) from($(alias), coll).where($(alias.getId()).eq(query.getId()));	
	}

}
