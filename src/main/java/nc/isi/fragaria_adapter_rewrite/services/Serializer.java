package nc.isi.fragaria_adapter_rewrite.services;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

public interface Serializer<T> {
	Collection<T> serialize(Collection<Entity> objects);

	T serialize(Entity object);

	<E extends Entity> Collection<E> deSerialize(Collection<T> objects,
			Class<E> entityClass);

	<E extends Entity> E deSerialize(T object, Class<E> entityClass);

}
