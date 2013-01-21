package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Entity extends Serializable, Partialable {

	public static final String ID = "id";
	public static final String REV = "rev";
	public static final String TYPES = "types";

	public <T> T readProperty(Class<T> propertyType, String propertyName);

	public <T> Collection<T> readCollection(Class<T> collectionGenericType,
			String collectionName);

	public void writeProperty(String propertyName, Object value);

	public State getState();

	public void setState(State state);

	public List<String> getTypes();

	public ObjectNode toJSON();

	public void registerListener(Object o);

	public void unregisterListener(Object listener);

	public EntityMetadata getMetadata();
	
	public Session getSession();

	public void setSession(Session session);

}
