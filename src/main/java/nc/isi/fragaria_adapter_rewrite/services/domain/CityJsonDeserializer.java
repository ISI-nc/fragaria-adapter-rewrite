package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.io.IOException;

import nc.isi.fragaria_adapter_rewrite.City;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CityJsonDeserializer extends JsonDeserializer<City> {
	private final EntityBuilder entityBuilder;

	public CityJsonDeserializer(EntityBuilder entityBuilder) {
		this.entityBuilder = entityBuilder;
	}

	@Override
	public City deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ObjectNode node = jp.readValueAsTree();
		return entityBuilder.build(node, City.class);
	}

}
