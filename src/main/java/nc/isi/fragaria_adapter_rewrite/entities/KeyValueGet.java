package nc.isi.fragaria_adapter_rewrite.entities;

import java.util.NoSuchElementException;

public interface KeyValueGet<K, V> {

	/**
	 * Get the value associated to the given <code>key</code>.
	 * 
	 * <p>
	 * The behaviour if key has no value is unspecified here. May return null or
	 * a {@link NoSuchElementException}.
	 * 
	 * @param key
	 *            The key under the requested value is.
	 * @return Th value associated to the key.
	 */
	public V get(K key);

}
