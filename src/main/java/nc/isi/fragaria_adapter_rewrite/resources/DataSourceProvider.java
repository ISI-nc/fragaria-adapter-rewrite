package nc.isi.fragaria_adapter_rewrite.resources;

public interface DataSourceProvider {

	/**
	 * Fournit les datasources depuis les contributions dans l'AppModule
	 * 
	 * @param key
	 * @return
	 */
	Datasource provide(String key);

}