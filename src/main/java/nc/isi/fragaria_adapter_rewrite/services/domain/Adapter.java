package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.LinkedList;

public interface Adapter {

	public <T extends Entity> CollectionQueryResponse<T> executeQuery(
			Query<T> query);

	public <T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(
			Query<T> query);

	public void post(Entity... entities);

	public void post(LinkedList<Entity> entities);

}
