package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Collection;

public interface AdapterManager {
	public <T extends Entity> QueryResponse<T> executeQuery(Query<T> query);

	public void post(Entity... entities);

	public void post(Collection<Entity> entities);

}
