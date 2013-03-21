package nc.isi.fragaria_adapter_rewrite.dao;

import java.util.Collection;
import java.util.UUID;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author bjonathas
 * 
 *         Classe permettant de récupérer une ou des entities via les méthodes
 *         get et d'en créer de nouvelles via la méthode create. Les
 *         modifications sont enregistrés via void
 *         recordPropertyChange(PropertyChangeEvent e). Entity object) qui
 *         écoute les entités appartenant à la session et qui est appelé à
 *         chaque modification. La méthode post permet d'appliquer toutes les
 *         opérations sur les datasources via les adapters. Logique Parent child
 *         pas réellement implémentée.
 * 
 */

public interface Session {

	<T extends Entity> Collection<T> get(Query<T> query, boolean cache);

	<T extends Entity> Collection<T> get(Query<T> query);

	<T extends Entity> T getUnique(Query<T> query, boolean cache);

	<T extends Entity> T getUnique(Query<T> query);

	<T extends Entity> T create(Class<T> entityClass);

	<T extends Entity> T create(Class<T> entityClass, Object... params);

	void delete(Entity... entity);

	<T extends Entity> void delete(Collection<T> entity);

	Session post();

	Session cancel();

	UUID getId();

	<T extends Entity> T create(ObjectNode node);

}
