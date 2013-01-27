package nc.isi.fragaria_adapter_rewrite.resources;

/**
 * Description d'une Datasource
 * 
 * @author justin
 * 
 */
public interface Datasource {

	/**
	 * La clé unique de la datasource
	 * 
	 * @return
	 */
	String getKey();

	/**
	 * les métadonnées de la datasource
	 * 
	 * @return
	 */
	DataSourceMetadata getDsMetadata();

}
