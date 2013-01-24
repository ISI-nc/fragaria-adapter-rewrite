package nc.isi.fragaria_adapter_rewrite.utils.jackson;

import java.io.IOException;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class EntityJsonSerializer extends JsonSerializer<Entity> {
	@Override
	public void serialize(Entity value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException {
		jgen.writeTree(value.toJSON());
	}

	@Override
	public Class<Entity> handledType() {
		return Entity.class;
	}

}
