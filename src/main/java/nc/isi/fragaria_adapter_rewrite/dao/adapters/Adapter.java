package nc.isi.fragaria_adapter_rewrite.dao.adapters;

import java.util.List;

import nc.isi.fragaria_adapter_rewrite.dao.CollectionQueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.Query;
import nc.isi.fragaria_adapter_rewrite.dao.UniqueQueryResponse;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.enums.State;
import nc.isi.fragaria_adapter_rewrite.resources.Datasource;

/**
 * Inteface pour définir un adapter qui sera utilisé par l'
 * {@link AdapterManager} pour gérer les communications avec une
 * {@link Datasource} donnée
 * 
 * @author jmaltat
 * 
 */
public interface Adapter {

	<T extends Entity> CollectionQueryResponse<T> executeQuery(Query<T> query);

	<T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(Query<T> query);

	/**
	 * Post un ensemble d'entités ayant été altérées pendant le traitement, On
	 * peut connaître le type d'altération pour chaque entité par {@link State}
	 * Les entités peuvent être présentes plusieurs fois dans la liste si elles
	 * ont subies plusieurs altérations c'est à chaque {@link Adapter} de gérer
	 * les opérations de la file selon son besoin
	 * 
	 * @param entities
	 */
	void post(Entity... entities);

	void post(List<Entity> entities);

}
