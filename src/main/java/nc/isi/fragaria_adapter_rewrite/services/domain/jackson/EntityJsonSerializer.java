package nc.isi.fragaria_adapter_rewrite.services.domain.jackson;

import java.io.IOException;

import nc.isi.fragaria_adapter_rewrite.services.domain.Entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class EntityJsonSerializer extends JsonSerializer<Entity> {

	@Override
	public void serialize(Entity value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeTree(value.toJSON());
	}

	@Override
	public Class<Entity> handledType() {
		return Entity.class;
	}

}
