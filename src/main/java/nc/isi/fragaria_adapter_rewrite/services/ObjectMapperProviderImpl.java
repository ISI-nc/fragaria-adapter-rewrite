package nc.isi.fragaria_adapter_rewrite.services;

import nc.isi.fragaria_adapter_rewrite.utils.jackson.EntityJacksonModule;

import org.ektorp.CouchDbConnector;
import org.ektorp.impl.jackson.EktorpJacksonModule;

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

	protected void init(ObjectMapper objectMapper) {
		objectMapper.registerModule(new JodaModule());
		objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		objectMapper.registerModule(entityJacksonModule);
	}

	@Override
	public ObjectMapper provide() {
		return objectMapper;
	}

	@Override
	public ObjectMapper createObjectMapper() {
		return provide();
	}

	@Override
	public ObjectMapper createObjectMapper(CouchDbConnector connector) {
		ObjectMapper specificOM = new ObjectMapper();
		init(specificOM);
		specificOM
				.registerModule(new EktorpJacksonModule(connector, specificOM));
		return provide();
	}

}
