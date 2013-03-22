package nc.isi.fragaria_adapter_rewrite.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectNodeKVGet implements KeyValueGet<String, JsonNode> {

	private final ObjectNode node;

	public ObjectNodeKVGet(ObjectNode node) {
		this.node = node;
	}

	@Override
	public JsonNode get(String key) {
		return node.get(key);
	}

}
