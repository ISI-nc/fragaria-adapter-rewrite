package nc.isi.fragaria_adapter_rewrite.services.domain.session;

import java.util.Collection;
import java.util.UUID;

import nc.isi.fragaria_adapter_rewrite.services.domain.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.services.domain.Entity;
import nc.isi.fragaria_adapter_rewrite.services.domain.OperationType;
import nc.isi.fragaria_adapter_rewrite.services.domain.Query;

/**
 * 
 * @author bjonathas 
 * 
 * Classe permettant de récupérer une ou des entities via les
 * méthodes get et d'en créer de nouvelles via la méthode create. Les
 * modifications sont enregistrés via void recordPropertyChange(PropertyChangeEvent e).
 * Entity object) qui écoute les entités appartenant à la session et qui
 * est appelé à chaque modification. La méthode post permet d'appliquer toutes les
 * opérations sur les datasources via les adapters. Logique Parent child
 * pas réellement implémentée.
 * 
 */

public interface Session {

	public <T extends Entity> Collection<T> get(Query<T> query);

	public <T extends Entity>  T getUnique(Query<T> query);

	public <T extends Entity>  T create(Class<T> entityClass);

	public void delete(Entity... entity);

	public <T extends Entity> void delete(Collection<T> entity);

	public Session post();

	public Session cancel();

	/**
	 * register
	 * 
	 * C'est la methode principale de la session qui enregistre les entités nouvelles, modifées ou à supprimer
	 * de sorte à pouvoir reconstituer les objets tels qu'ils sont dans la session en cours. 
	 * Un objet ne peut se trouver que dans une seule map à la fois. 
	 * Les opérations sont elles ajouter à une file (queue) et seront ensuite traitées par les {@link AdapterManager}
	 * 
	 * @param o
	 * @param object
	 */
	public <T extends Entity>  void register(OperationType o, T object);

	public UUID getId();


}
