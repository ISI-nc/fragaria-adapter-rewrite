package nc.isi.fragaria_adapter_rewrite.resources;

import java.util.Map;

/**
 * Une interface pour définir un DatasourceLoader
 * 
 * @author justin
 * 
 */
public interface SpecificDsLoader {
	Map<String, Datasource> getDs();
}
