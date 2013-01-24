package nc.isi.fragaria_adapter_rewrite.ressources;

import java.util.Map;


/**
 * 
 * @author bjonathas Service permettant de charger toutes des datasources à
 *         utiliser dans le projet. Le chargement s'effectue grace à des
 *         services plus spécifics permettant de construir ces datasources à
 *         partir de différentes sources de données (ex : fichier.yaml). Pour
 *         rajouter une source de données, il suffit de contribuer à ce service
 *         le specificDsLoader correspondant à la source de données (ex :
 *         yamlDsLoader)
 */
public interface MasterDsLoader {
	Map<String, Datasource> getDs();
}
