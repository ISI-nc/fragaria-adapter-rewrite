package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.LinkedList;

/**
 * Fournit une interface générale au dessus des {@link Adapter} Réparti les
 * demandes en fonction des {@link Datasource} des {@link Entity} passées en
 * paramètres
 * 
 * @see DsKey
 * @author jmaltat
 * 
 */
public interface AdapterManager {
	public <T extends Entity> QueryResponse<T> executeQuery(Query<T> query);

	public <T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(
			Query<T> query);

	public void post(Entity... entities);

	public void post(LinkedList<Entity> entities);

}
