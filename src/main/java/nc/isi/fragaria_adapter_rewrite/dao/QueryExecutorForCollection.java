package nc.isi.fragaria_adapter_rewrite.dao;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

/**
 * 
 * @author bjonathas
 * 
 * @param <Class>
 * 
 *            service permettant l'exécution des queries sur des collections
 *            d'entity
 */
public interface QueryExecutorForCollection {
	<T extends Entity> T getUnique(
			AbstractQuery<T> query, Collection<T> collection);
}
