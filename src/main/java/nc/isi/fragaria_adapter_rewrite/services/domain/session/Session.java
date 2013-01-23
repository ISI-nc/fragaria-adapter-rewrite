package nc.isi.fragaria_adapter_rewrite.services.domain.session;

import java.util.Collection;
import java.util.UUID;

import nc.isi.fragaria_adapter_rewrite.services.domain.Entity;
import nc.isi.fragaria_adapter_rewrite.services.domain.OperationType;
import nc.isi.fragaria_adapter_rewrite.services.domain.Query;

/**
 * 
 * @author bjonathas Classe permettant de récupérer une ou des entities via les
 *         méthodes get et d'en créer de nouvelles via la méthode create. Les
 *         modifications sont enregistrés via void register(OperationType o,
 *         Entity object) qui écoute les entités appartenant à la session et qui
 *         est appelé à chaque création/délétion. C'est le point central de la
 *         gestion des opérations. La méthode post permet d'appliquer toutes les
 *         opérations sur les datasources via les adapters. Logique Parent child
 *         pas réellement implémentée.
 * 
 */

public interface Session {

	public <T extends Entity> Collection<T> get(Query<T> query);

	public <T extends Entity> T getUnique(Query<T> query);

	public <T extends Entity> T create(Class<T> entityClass);

	public void delete(Entity... entity);

	public void delete(Collection<Entity> entity);

	public Session post();

	public Session cancel();

	public void register(OperationType o, Entity object);

	public Session createChild();

	public UUID getId();

	public void addChild(Session session);

}
