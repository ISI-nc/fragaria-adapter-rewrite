package nc.isi.fragaria_adapter_rewrite.services.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperProvider {

	public ObjectMapper provide();

}
