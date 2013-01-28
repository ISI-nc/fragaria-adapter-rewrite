package nc.isi.fragaria_adapter_rewrite.resources;

import java.util.Collection;

public interface DataSourceProvider {

	/**
	 * Fournit les datasources depuis les contributions dans l'AppModule
	 * 
	 * @param key
	 * @return
	 */
	Datasource provide(String key);

	/**
	 * renvoie l'ensemble des {@link Datasource} connues
	 * 
	 * @return
	 */
	Collection<Datasource> datasources();

}