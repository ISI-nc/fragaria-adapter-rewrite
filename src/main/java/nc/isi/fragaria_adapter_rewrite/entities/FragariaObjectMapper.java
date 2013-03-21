package nc.isi.fragaria_adapter_rewrite.entities;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

	@SuppressWarnings("unchecked")
	public <E extends Entity> Class<E> getEntityClass(ObjectNode node)
			throws JsonProcessingException, ClassNotFoundException {
		ArrayNode types = (ArrayNode) node.get(Entity.TYPES);
		checkArgument(
				types != null,
				"impossible to know objet class without types defined (objectnode : %s)",
				node);
		String realType = objectMapper.treeToValue(types.get(0), String.class);
		return (Class<E>) Class.forName(realType);
	}

}
