package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.LinkedList;

public interface AdapterManager {
	public <T extends Entity> QueryResponse<T> executeQuery(Query<T> query);

	public void post(Entity... entities);

	public void post(LinkedList<Entity> entities);

}
