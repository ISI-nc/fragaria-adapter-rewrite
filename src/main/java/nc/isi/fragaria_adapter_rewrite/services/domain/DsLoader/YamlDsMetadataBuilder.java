package nc.isi.fragaria_adapter_rewrite.services.domain.DsLoader;

import java.util.LinkedHashMap;

import nc.isi.fragaria_adapter_rewrite.services.domain.DataSourceMetadata;

public interface YamlDsMetadataBuilder {
	DataSourceMetadata getDsMetadataFrom(LinkedHashMap<String,Object> map);
}
