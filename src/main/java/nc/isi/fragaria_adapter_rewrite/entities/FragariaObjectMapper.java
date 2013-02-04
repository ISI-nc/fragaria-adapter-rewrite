package nc.isi.fragaria_adapter_rewrite.entities;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public enum FragariaObjectMapper {
	INSTANCE;

	private final ObjectMapper objectMapper;

	private FragariaObjectMapper() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
	}

	public ObjectMapper get() {
		return objectMapper;
	}

}
