package nc.isi.fragaria_adapter_rewrite.services.domain;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class ObjectMapperProviderImpl implements ObjectMapperProvider {
	private final ObjectMapper objectMapper = new ObjectMapper();

	public ObjectMapperProviderImpl() {
		objectMapper.registerModule(new JodaModule());
		objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
	}

	@Override
	public ObjectMapper provide() {
		return objectMapper;
	}

}
