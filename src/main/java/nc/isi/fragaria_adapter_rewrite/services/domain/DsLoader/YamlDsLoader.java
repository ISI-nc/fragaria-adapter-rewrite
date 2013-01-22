package nc.isi.fragaria_adapter_rewrite.services.domain.DsLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

import nc.isi.fragaria_adapter_rewrite.services.domain.ConnectionDataBuilder;
import nc.isi.fragaria_adapter_rewrite.services.domain.DataSourceMetadata;
import nc.isi.fragaria_adapter_rewrite.services.domain.Datasource;
import nc.isi.fragaria_adapter_rewrite.services.domain.DatasourceImpl;
import nc.isi.fragaria_adapter_rewrite.services.domain.ResourceFinder;

import com.beust.jcommander.internal.Maps;
import com.beust.jcommander.internal.Sets;
/**
 * 
 * @author bjonathas
 * Specific DsLoader permettant de charger une datasource depuis un fichier yaml
 */
public class YamlDsLoader implements SpecificDsLoader {
	private static final String YAML_REG_EXP = ".*\\.yaml";
	private final Set<File> dsFiles = Sets.newHashSet();
	private final YamlSerializer serializer;
	private final ConnectionDataBuilder builder;
	
	public YamlDsLoader(ResourceFinder finder,YamlSerializer serializer,ConnectionDataBuilder builder) {
		this.dsFiles.addAll(finder.getResourcesMatching(YAML_REG_EXP));
		this.serializer = serializer;
		this.builder = builder;
	}

	@Override
	public Map<String, Datasource> getDs() {
		Map<String, Datasource> map = Maps.newHashMap();
		for(File dsFile : dsFiles){
			String dsKey = getDsKeyFrom(dsFile.getName());
			try {
				DataSourceMetadata dsMeta = buildGenericDatasourceMetadataFrom(
						serializer.serializeFromFileAs(dsFile,YamlDatasourceMetadata.class));
				map.put(dsKey,new DatasourceImpl(dsKey,dsMeta));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}
		return map;		
	}


	private String getDsKeyFrom(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}

	private DataSourceMetadata buildGenericDatasourceMetadataFrom(YamlDatasourceMetadata yamlDs) {
		return new DataSourceMetadata(
				yamlDs.getType(), builder.build(
						yamlDs.getType(), yamlDs.getConnectionData()), yamlDs.isCanEmbed());	
	}

}
