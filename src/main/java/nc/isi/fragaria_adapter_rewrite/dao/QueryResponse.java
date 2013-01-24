package nc.isi.fragaria_adapter_rewrite.dao;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

public interface QueryResponse<T extends Entity> {

	public Object getResponse();

}
