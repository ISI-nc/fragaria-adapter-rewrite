package nc.isi.fragaria_adapter_rewrite.entities;

import nc.isi.fragaria_adapter_rewrite.utils.jackson.EntityJacksonModule;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public enum FragariaObjectMapper {
	INSTANCE;

	private final ObjectMapper objectMapper;

	private FragariaObjectMapper() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
		objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		objectMapper.registerModule(new EntityJacksonModule());
	}

	public ObjectMapper get() {
		return objectMapper;
	}

}
