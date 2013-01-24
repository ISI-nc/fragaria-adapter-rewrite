package nc.isi.fragaria_adapter_rewrite.resources;

import nc.isi.fragaria_adapter_rewrite.couchdb.CouchdbConnectionData;

/**
 * Une interface pour définir les propriétés de connection d'une dataSource Pour
 * ajouter un type de dataSource au framework, il faudra contribuer à
 * {@link ConnectionDataBuilder} une implémentation de ConnectionData associée
 * au type de dataSource
 * 
 * Cette implémentation devra avoir un constructeur avec l'ensemble des
 * paramètres requis voir {@link CouchdbConnectionData} pour un exemple
 * 
 * 
 * @author jmaltat
 * 
 */
public interface ConnectionData {

}
