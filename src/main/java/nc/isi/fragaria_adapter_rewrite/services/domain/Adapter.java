package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Collection;

public interface Adapter {

	public <T extends Entity> QueryResponse<T> executeQuery(Query<T> query);

	public void post(Object... objects);

	public void post(Collection<Object> objects);

}
