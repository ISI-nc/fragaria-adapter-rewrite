package nc.isi.fragaria_adapter_rewrite.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import nc.isi.fragaria_adapter_rewrite.dao.Session;
import nc.isi.fragaria_adapter_rewrite.entities.views.View;
import nc.isi.fragaria_adapter_rewrite.enums.State;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Entity extends Serializable, Partialable {

	String ID = "id";
	String REV = "rev";
	String TYPES = "types";

	State getState();

	void setState(State state);

	List<String> getTypes();

	ObjectNode toJSON();

	ObjectNode toJSON(Class<? extends View> view);

	void registerListener(Object o);

	void unregisterListener(Object listener);

	EntityMetadata metadata();

	Session getSession();

	void attributeSession(Session session);

	void writeProperty(String propertyName, Object value);

	<T> T readProperty(Class<T> propertyType, String propertyName);

	<T> Collection<T> readCollection(Class<T> collectionGenericType,
			String collectionName);

	<T> Boolean add(String collectionName, T element, Class<T> collectionType);

	<T> Boolean remove(String collectionName, T element, Class<T> collectionType);
	
	void prepareForCommit();
}
