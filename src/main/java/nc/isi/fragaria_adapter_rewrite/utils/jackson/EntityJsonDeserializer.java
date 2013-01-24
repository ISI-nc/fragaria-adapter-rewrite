package nc.isi.fragaria_adapter_rewrite.utils.jackson;

import java.io.IOException;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EntityJsonDeserializer<T extends Entity> extends
		JsonDeserializer<T> {
	private final EntityBuilder entityBuilder;
	private final Class<T> type;

	public EntityJsonDeserializer(EntityBuilder entityBuilder, Class<T> type) {
		this.entityBuilder = entityBuilder;
		this.type = type;
	}

	@Override
	public T deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException {
		ObjectNode objectNode = jp.readValueAsTree();
		return entityBuilder.build(objectNode, type);
	}

}
