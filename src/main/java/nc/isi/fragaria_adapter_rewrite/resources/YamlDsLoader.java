package nc.isi.fragaria_adapter_rewrite.resources;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 
 * @author bjonathas Specific DsLoader permettant de charger une datasource
 *         depuis un fichier yaml
 */
public class YamlDsLoader implements SpecificDsLoader {
	private static final String YAML_REG_EXP = ".*\\.yaml";
	private final Map<String, Datasource> map = Maps.newHashMap();;
	private final YamlSerializer serializer;
	private final ConnectionDataBuilder builder;

	public YamlDsLoader(ResourceFinder finder, YamlSerializer serializer,
			ConnectionDataBuilder builder) {
		this.serializer = serializer;
		this.builder = builder;
		for (File dsFile : finder.getResourcesMatching(YAML_REG_EXP)) {
			String dsKey = getDsKey(dsFile.getName());
			try {
				map.put(dsKey, new DatasourceImpl(dsKey,
						buildDsMetadata(dsFile)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Map<String, Datasource> getDs() {
		return map;
	}

	private String getDsKey(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}

	private DataSourceMetadata buildDsMetadata(File dsFile) throws IOException {
		YamlDatasourceMetadata yamlDs = serializer.serialize(dsFile,
				YamlDatasourceMetadata.class);
		return new DataSourceMetadata(yamlDs.getType(), builder.build(
				yamlDs.getType(), yamlDs.getConnectionData().values()),
				yamlDs.canEmbed());
	}

}
