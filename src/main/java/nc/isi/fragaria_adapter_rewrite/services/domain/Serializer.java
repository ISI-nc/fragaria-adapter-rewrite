package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Collection;

public interface Serializer<T> {
	public Collection<T> serialize(Collection<Entity> objects);

	public Collection<Entity> deSerialize(Collection<T> objects);

}
