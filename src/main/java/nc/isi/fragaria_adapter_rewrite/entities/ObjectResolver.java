package nc.isi.fragaria_adapter_rewrite.entities;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.entities.views.View;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ObjectResolver {
	public <T> T resolve(ObjectNode node, Class<T> propertyType,
			String propertyName, Entity entity);

	public <T> Collection<T> resolveCollection(ObjectNode node,
			Class<T> propertyType, String propertyName, Entity entity);

	void write(ObjectNode node, String propertyName, Object value, Entity entity);

	public ObjectNode clone(ObjectNode node, Class<? extends View> view,
			Entity entity);

}
