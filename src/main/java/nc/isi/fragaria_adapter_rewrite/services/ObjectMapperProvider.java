package nc.isi.fragaria_adapter_rewrite.services;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperProvider {

	/**
	 * Fournit un ObjectMapper configuré pour serializer/deserializer
	 * {@link Entity} et {@link DateTime}
	 * 
	 * L {@link ObjectMapper} est unique au sein de l'application
	 * 
	 * @return
	 */
	ObjectMapper provide();

}
