package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Collection;
import java.util.UUID;

public interface Session {

	public <T extends Entity> Collection<T> get(Query<T> query);

	public <T extends Entity> T getUnique(Query<T> query);

	public <T extends Entity> T create(Class<T> entityClass);

	public void delete(Entity... entity);

	public void delete(Collection<Entity> entity);

	public Session post();

	public Session cancel();

	public void register(OperationType o, Object object);

	public Session createChild();

	public UUID getId();

	public void addChild(Session session);

	public AdapterManager getAdapterManager();
	
	public EntityBuilder getEntityBuilder();

}
