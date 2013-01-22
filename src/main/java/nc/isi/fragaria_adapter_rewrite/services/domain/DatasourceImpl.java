package nc.isi.fragaria_adapter_rewrite.services.domain;

public class DatasourceImpl implements Datasource{

	private final String key;
	private final DataSourceMetadata dsMetadata;
	
	
	public DatasourceImpl(String key, DataSourceMetadata dsMetadata) {
		super();
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
