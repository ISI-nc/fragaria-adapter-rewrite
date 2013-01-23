package nc.isi.fragaria_adapter_rewrite.services.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import nc.isi.fragaria_adapter_rewrite.services.domain.GenericViews.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

/**
 * Classe de base pour toutes les entités Gère la complétion des objets
 * (lazy-loading and partial)
 * 
 * @author jmaltat
 * 
 */
public abstract class AbstractEntity implements Entity {
	private enum Action {
		READ("lire"), WRITE("écrire");

		private final String s;

		private Action(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}
	}

	private final ObjectNode objectNode;
	private final ObjectResolver objectResolver;
	private final Map<String, Object> cache = Maps.newHashMap();
	private final EntityMetadata entityMetadata;
	private final EventBus eventBus = new EventBus();
	private final LinkedList<String> types;
	private State state = State.NEW;
	private Completion completion;
	private Session session;
	private final String tempId = UUID.randomUUID().toString();

	public AbstractEntity(ObjectNode objectNode, ObjectResolver objectResolver,
			EntityMetadataFactory entityMetadataFactory) {
		this.objectNode = checkNotNull(objectNode);
		this.objectResolver = objectResolver;
		this.entityMetadata = entityMetadataFactory.create(getClass());
		this.types = initTypes();
		setId(tempId);
	}

	private LinkedList<String> initTypes() {
		LinkedList<String> types = new LinkedList<>();
		for (Class<?> type = getClass(); Entity.class.isAssignableFrom(type); type = type
				.getSuperclass()) {
			types.addLast(type.getName());
		}
		writeProperty(TYPES, types);
		return types;
	}

	@Override
	public ObjectNode toJSON() {
		return objectNode.deepCopy();
	}

	@Override
	public <T> T readProperty(Class<T> propertyType, String propertyName) {
		checkGlobalSanity(propertyName, Action.READ);
		if (!cache.keySet().contains(propertyName))
			cache.put(propertyName, objectResolver.resolve(objectNode,
					propertyType, propertyName, this));
		return propertyType.cast(cache.get(propertyName));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Collection<T> readCollection(Class<T> collectionGenericType,
			String collectionName) {
		checkGlobalSanity(collectionName, Action.READ);
		if (!cache.containsKey(collectionName))
			cache.put(collectionName, objectResolver.resolveCollection(
					objectNode, collectionGenericType, collectionName, this));
		return (Collection<T>) cache.get(collectionName);
	}

	@Override
	public void writeProperty(String propertyName, Object value) {
		checkGlobalSanity(propertyName, Action.WRITE);
		Object oldValue = cache.get(propertyName);
		objectResolver.write(objectNode, propertyName, value, this);
		cache.put(propertyName, value);
		PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this,
				propertyName, oldValue, value);
		eventBus.post(propertyChangeEvent);
		if (cache.values().containsAll(entityMetadata.propertyNames()))
			setCompletion(Completion.FULL);
	}

	@Override
	public State getState() {
		return this.state;
	}

	@Override
	public void setState(State state) {
		checkChange(this.state, state);
		this.state = state;
	}

	private void checkChange(State oldState, State newSate) {
		boolean error = oldState == State.DELETED;
		if (!error)
			error = oldState == State.MODIFIED && newSate == State.NEW;
		if (error)
			throw new StateChangeException(oldState, newSate);

	}

	@Override
	public List<String> getTypes() {
		return types;
	}

	@Override
	public void registerListener(Object o) {
		eventBus.register(o);
	}

	@Override
	public void unregisterListener(Object listener) {
		eventBus.unregister(listener);

	}

	@Override
	public Completion getCompletion() {
		return this.completion;
	}

	@Override
	@JsonIgnore
	public void setCompletion(Completion completion) {
		this.completion = completion;
	}

	protected void checkGlobalSanity(String propertyName, Action action) {
		checkState(state != State.DELETED,
				"impossible de %s les propriétés d'un objet effacé", action);
		checkArgument(entityMetadata.propertyNames().contains(propertyName),
				"La propriété %s n'est pas connu pour les objets de type %s",
				propertyName, getClass());
	}

	@JsonView(Id.class)
	@JsonProperty("_id")
	public String getId() {
		return readProperty(String.class, ID);
	}

	@JsonProperty("_id")
	public void setId(String id) {
		writeProperty(ID, id);
	}

	@JsonProperty("_rev")
	public String getRev() {
		return readProperty(String.class, REV);
	}

	@JsonProperty("_rev")
	public void setRev(String rev) {
		writeProperty(REV, rev);
	}

	@Override
	public EntityMetadata getMetadata() {
		return entityMetadata;
	}

	@Override
	public Session getSession() {
		return session;
	}

	/**
	 * seté la {@link Session} d'une {@link Entity} enregistre cette session
	 * comme listener de l'entité et supprime la session précédente des listener
	 */
	@Override
	public void setSession(Session session) {
		if (this.session != null)
			unregisterListener(this.session);
		this.session = session;
		registerListener(session);
	}

	@Override
	public ObjectNode toJSON(Class<? extends View> view) {
		return objectResolver.clone(objectNode, view, this);
	}

}
