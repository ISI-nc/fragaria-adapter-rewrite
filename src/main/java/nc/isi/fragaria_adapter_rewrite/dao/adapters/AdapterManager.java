package nc.isi.fragaria_adapter_rewrite.dao.adapters;

import java.util.List;

import nc.isi.fragaria_adapter_rewrite.annotations.DsKey;
import nc.isi.fragaria_adapter_rewrite.dao.Query;
import nc.isi.fragaria_adapter_rewrite.dao.QueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.UniqueQueryResponse;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.resources.Datasource;

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

	public void post(List<Entity> entities);

}
