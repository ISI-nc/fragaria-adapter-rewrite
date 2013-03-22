package nc.isi.fragaria_adapter_rewrite.utils;

import java.util.Map.Entry;

public class MyEntry<K, V> implements Entry<K, V> {
	private final K key;
	private V value;

	public MyEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		return value;
	}

	@Override
	public String toString() {
		return "( key: " + key + ", value:" + value + ")";
	}

}
