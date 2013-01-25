package nc.isi.fragaria_adapter_rewrite.services;

import nc.isi.fragaria_adapter_rewrite.utils.jackson.EntityJacksonModule;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class ObjectMapperProviderImpl implements ObjectMapperProvider {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final EntityJacksonModule entityJacksonModule;

	public ObjectMapperProviderImpl(EntityJacksonModule entityJacksonModule) {
		this.entityJacksonModule = entityJacksonModule;
		init(objectMapper);
	}

	protected final void init(ObjectMapper objectMapper) {
		objectMapper.registerModule(new JodaModule());
		objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		objectMapper.registerModule(entityJacksonModule);
	}

	@Override
	public ObjectMapper provide() {
		return objectMapper;
	}

	public ObjectMapper createObjectMapper() {
		return provide();
	}

}
