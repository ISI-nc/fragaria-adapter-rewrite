package nc.isi.fragaria_adapter_rewrite.utils.jackson;

import java.io.IOException;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class EntityJsonSerializer extends JsonSerializer<Entity> {
	private static final Logger LOGGER = Logger
			.getLogger(EntityJsonSerializer.class);

	@Override
	public void serialize(Entity value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException {
		LOGGER.info("serialize : " + value);
		jgen.writeTree(value.toJSON());
	}

	@Override
	public Class<Entity> handledType() {
		return Entity.class;
	}

}
