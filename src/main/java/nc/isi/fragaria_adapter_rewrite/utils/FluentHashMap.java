package nc.isi.fragaria_adapter_rewrite.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

public class FluentHashMap<K, V> implements Map<K, V> {
	private final Map<K, V> map = Maps.newHashMap();

	public FluentHashMap() {

	}

	public FluentHashMap(K key, V value) {
		map.put(key, value);
	}

	public FluentHashMap<K, V> append(K key, V value) {
		map.put(key, value);
		return this;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public V remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public V put(K key, V value) {
		return map.put(key, value);
	}

}
