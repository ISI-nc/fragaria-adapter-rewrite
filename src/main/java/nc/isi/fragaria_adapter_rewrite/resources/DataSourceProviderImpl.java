package nc.isi.fragaria_adapter_rewrite.resources;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Map;

public class DataSourceProviderImpl implements DataSourceProvider {
	private final Map<String, Datasource> map;

	public DataSourceProviderImpl(Map<String, Datasource> map) {
		this.map = map;
	}

	@Override
	public Datasource provide(String key) {
		return map.get(key);
	}

	@Override
	public Collection<Datasource> datasources() {
		return map.values();
	}

	@Override
	public void register(Datasource ds) {
		checkNotNull(ds);
		checkState(!map.containsKey(ds.getKey()), "la datasource existe déjà");
		map.put(ds.getKey(), ds);
	}

	@Override
	public void unregister(Datasource ds) {
		checkNotNull(ds);
		checkState(map.containsKey(ds.getKey()), "la datasource n'existe pas");
		map.remove(ds.getKey());
	}

}
