package nc.isi.fragaria_adapter_rewrite.services.domain;

import org.ektorp.impl.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperProvider extends ObjectMapperFactory {

	public ObjectMapper provide();

}
