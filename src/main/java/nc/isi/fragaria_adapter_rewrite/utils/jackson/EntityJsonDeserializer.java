package nc.isi.fragaria_adapter_rewrite.utils.jackson;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.google.common.base.Throwables;

public class EntityJsonDeserializer<T extends Entity> extends
		JsonDeserializer<T> {
	private static final Logger LOGGER = Logger
			.getLogger(EntityJsonDeserializer.class);
	private final Class<T> type;

	public EntityJsonDeserializer(Class<T> type) {
		this.type = type;
	}

	@Override
	public T deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException {
		LOGGER.info("deserialize : " + type);
		TreeNode treeNode = jp.readValueAsTree();
		ObjectNode objectNode = null;
		if (treeNode instanceof ObjectNode) {
			objectNode = (ObjectNode) treeNode;
			LOGGER.info("deserialized : " + type + " in : "
					+ objectNode.toString());

		} else {
			POJONode pojoNode = (POJONode) treeNode;
			LOGGER.info("deserialized with pojo : " + type + " in : "
					+ pojoNode);
			objectNode = (ObjectNode) pojoNode.getPojo();
		}
		try {
			return type.getConstructor(ObjectNode.class)
					.newInstance(objectNode);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw Throwables.propagate(e);
		}
	}

}
