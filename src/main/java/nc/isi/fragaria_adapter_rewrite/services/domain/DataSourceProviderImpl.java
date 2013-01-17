package nc.isi.fragaria_adapter_rewrite.services.domain;

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

}
