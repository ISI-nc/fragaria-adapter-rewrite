package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Collection;

public interface Serializer<T> {
	public Collection<T> serialize(Collection<Entity> objects);

	public T serialize(Entity object);

	public <E extends Entity> Collection<E> deSerialize(Collection<T> objects,
			Class<E> entityClass);

	public <E extends Entity> E deSerialize(T object, Class<E> entityClass);

}
