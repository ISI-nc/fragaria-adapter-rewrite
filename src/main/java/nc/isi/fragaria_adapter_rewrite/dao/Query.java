package nc.isi.fragaria_adapter_rewrite.dao;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;


public interface Query<T extends Entity> {

	public Class<T> getResultType();

}