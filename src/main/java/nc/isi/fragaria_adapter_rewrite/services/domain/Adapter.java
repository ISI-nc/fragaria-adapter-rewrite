package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.LinkedList;

public interface Adapter {

	public <T extends Entity> CollectionQueryResponse<T> executeQuery(
			Query<T> query);

	public <T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(
			Query<T> query);

	/**
	 * Post un ensemble d'entités ayant été altérées pendant le traitement, On
	 * peut connaître le type d'altération pour chaque entité par {@link State}
	 * Les entités peuvent être présente plusieurs fois dans la liste si elles
	 * ont subies plusieurs altérations c'est à chaque adapter de gérer les
	 * opérations de la file selon son besoin
	 * 
	 * @param entities
	 */
	public void post(Entity... entities);

	public void post(LinkedList<Entity> entities);

}
