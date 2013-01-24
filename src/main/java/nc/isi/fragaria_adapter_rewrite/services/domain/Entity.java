package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.io.Serializable;
import java.util.List;

import nc.isi.fragaria_adapter_rewrite.services.domain.session.Session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Entity extends Serializable, Partialable {

	public static final String ID = "id";
	public static final String REV = "rev";
	public static final String TYPES = "types";

	@JsonIgnore
	public State getState();

	public void setState(State state);

	public List<String> getTypes();

	public ObjectNode toJSON();

	public ObjectNode toJSON(Class<? extends View> view);

	public void registerListener(Object o);

	public void unregisterListener(Object listener);

	@JsonIgnore
	public EntityMetadata getMetadata();

	@JsonIgnore
	public Session getSession();

	public void setSession(Session session);

}
