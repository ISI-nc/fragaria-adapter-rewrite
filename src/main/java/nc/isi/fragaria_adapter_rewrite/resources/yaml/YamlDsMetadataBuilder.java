package nc.isi.fragaria_adapter_rewrite.resources.yaml;

import java.util.LinkedHashMap;

import nc.isi.fragaria_adapter_rewrite.resources.DataSourceMetadata;


public interface YamlDsMetadataBuilder {
	DataSourceMetadata getDsMetadataFrom(LinkedHashMap<String,Object> map);
}
