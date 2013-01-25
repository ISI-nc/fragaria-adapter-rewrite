package nc.isi.fragaria_adapter_rewrite.entities;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.entities.views.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ObjectResolver {
	<T> T resolve(ObjectNode node, Class<T> propertyType, String propertyName,
			Entity entity);

	/**
	 * Cherche dans le node si une collection existe pour {@code propertyName}
	 * en utilisant {@link EntityMetadata} et {@link ObjectMapper}
	 * 
	 * renvoie une collection vide si rien n'est trouvé
	 * 
	 * @param node
	 * @param propertyType
	 * @param propertyName
	 * @param entity
	 * @return
	 */
	<T> Collection<T> resolveCollection(ObjectNode node, Class<T> propertyType,
			String propertyName, Entity entity);

	void write(ObjectNode node, String propertyName, Object value, Entity entity);

	ObjectNode clone(ObjectNode node, Class<? extends View> view, Entity entity);

}
