package nc.isi.fragaria_adapter_rewrite.services.domain.session;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.services.domain.Entity;
import nc.isi.fragaria_adapter_rewrite.services.domain.Query;
/**
 * 
 * @author bjonathas
 *
 * @param <Class>
 * 
 * service permettant l'exécution des queries sur des 
 * collections d'entity
 */
public interface  QueryExecutorForCollection{
	 <T extends Entity> T getUniqueObjectFromEntityCollFor(Query<T> query,Collection<T> collection);
}
