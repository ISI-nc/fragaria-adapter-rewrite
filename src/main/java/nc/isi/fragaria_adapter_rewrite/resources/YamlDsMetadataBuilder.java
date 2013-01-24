package nc.isi.fragaria_adapter_rewrite.resources;

import java.util.LinkedHashMap;


public interface YamlDsMetadataBuilder {
	DataSourceMetadata getDsMetadataFrom(LinkedHashMap<String,Object> map);
}
