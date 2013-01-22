package nc.isi.fragaria_adapter_rewrite.services.domain.DsLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import nc.isi.fragaria_adapter_rewrite.services.domain.ConnectionDataBuilder;
import nc.isi.fragaria_adapter_rewrite.services.domain.DataSourceMetadata;
import nc.isi.fragaria_adapter_rewrite.services.domain.Datasource;
import nc.isi.fragaria_adapter_rewrite.services.domain.ResourceFinder;

import com.beust.jcommander.internal.Maps;

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
			String dsKey = getDsKeyFrom(dsFile.getName());
			try {
				map.put(dsKey, new DatasourceImpl(dsKey,
						buildDsMetadataFrom(dsFile)));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Map<String, Datasource> getDs() {
		return map;
	}

	private String getDsKeyFrom(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}

	private DataSourceMetadata buildDsMetadataFrom(File dsFile)
			throws FileNotFoundException {
		YamlDatasourceMetadata yamlDs = serializer.serializeFromFileAs(dsFile,
				YamlDatasourceMetadata.class);
		return new DataSourceMetadata(yamlDs.getType(), builder.build(
				yamlDs.getType(), yamlDs.getConnectionData()),
				yamlDs.canEmbed());
	}

}
