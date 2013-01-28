package nc.isi.fragaria_adapter_rewrite.resources;

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

	public Collection<Datasource> datasources() {
		return map.values();
	}

}
