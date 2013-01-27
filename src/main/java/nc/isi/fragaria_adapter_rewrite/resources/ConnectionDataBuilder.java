package nc.isi.fragaria_adapter_rewrite.resources;

import java.util.Collection;

/**
 * Un service pour construire les connectionData en fonction du type de
 * datasource et d'une liste de paramètres
 * 
 * Va chercher le constructeur publique prenant en paramètres les classes des
 * objets passés dans la liste
 * 
 * Les objets doivent être dans l'ordre du constructeur
 * 
 * @author justin
 * 
 */
public interface ConnectionDataBuilder {

	ConnectionData build(String dsType, Object... params);

	ConnectionData build(String dsType, Collection<Object> params);

}