package nc.isi.fragaria_adapter_rewrite.resources;

public class DatasourceImpl implements Datasource {

	private final String key;
	private final DataSourceMetadata dsMetadata;

	public DatasourceImpl(String key, DataSourceMetadata dsMetadata) {
		this.key = key;
		this.dsMetadata = dsMetadata;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public DataSourceMetadata getDsMetadata() {
		return dsMetadata;
	}

}
