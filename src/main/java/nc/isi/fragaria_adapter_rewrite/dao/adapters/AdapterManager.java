package nc.isi.fragaria_adapter_rewrite.dao.adapters;

import java.util.List;

import nc.isi.fragaria_adapter_rewrite.annotations.DsKey;
import nc.isi.fragaria_adapter_rewrite.dao.Query;
import nc.isi.fragaria_adapter_rewrite.dao.QueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.UniqueQueryResponse;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewConfig;
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
	<T extends Entity> QueryResponse<T> executeQuery(Query<T> query);

	<T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(Query<T> query);

	void post(Entity... entities);

	void post(List<Entity> entities);

	Boolean exist(ViewConfig viewConfig, EntityMetadata entityMetadata);

	Boolean exist(ViewConfig viewConfig, Class<? extends Entity> entityClass);

	void buildView(ViewConfig viewConfig, EntityMetadata entityMetadata);

	void buildView(ViewConfig viewConfig, Class<? extends Entity> entityClass);

}
