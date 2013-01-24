package nc.isi.fragaria_adapter_rewrite.services;

import org.ektorp.impl.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperProvider extends ObjectMapperFactory {

	public ObjectMapper provide();

}
