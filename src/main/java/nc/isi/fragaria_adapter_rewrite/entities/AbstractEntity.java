package nc.isi.fragaria_adapter_rewrite.entities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import nc.isi.fragaria_adapter_rewrite.annotations.BackReference;
import nc.isi.fragaria_adapter_rewrite.annotations.InView;
import nc.isi.fragaria_adapter_rewrite.dao.Session;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericEmbedingViews;
import nc.isi.fragaria_adapter_rewrite.enums.Completion;
import nc.isi.fragaria_adapter_rewrite.enums.State;
import nc.isi.fragaria_adapter_rewrite.exceptions.StateChangeException;
import nc.isi.fragaria_adapter_rewrite.services.EntityMetadataProvider;
import nc.isi.fragaria_adapter_rewrite.utils.DefaultRegistry;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

/**
 * Classe de base pour toutes les entités Gère la complétion des objets
 * (lazy-loading and partial)
 * 
 * @author jmaltat
 * 
 */

public abstract class AbstractEntity extends ObjectNodeWrapper {
	private static final Logger LOGGER = Logger.getLogger(AbstractEntity.class);

	private enum Action {
		READ("lire"), WRITE("écrire"), ADD("Ajouté"), REMOVE("enlevé");

		private final String s;

		private Action(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}
	}

	private final Map<String, Object> cache = Maps.newHashMap();
	private EntityMetadata entityMetadata = DefaultRegistry.getService(
			EntityMetadataProvider.class).provide(getClass());
	private final EventBus eventBus = new EventBus();
	private State state = State.COMMITED;
	private Completion completion = Completion.PARTIAL;
	private Session session;
	private boolean typesInitialized = false;
	private final String tempId = UUID.randomUUID().toString();

	public AbstractEntity() {
		super();
		state = State.NEW;
	}

	public AbstractEntity(ObjectNode node) {
		super(node);
	}

	private List<String> initTypes() {
		LOGGER.debug("initializing types");
		LinkedList<String> tempTypes = new LinkedList<>();
		for (Class<?> type = getClass(); Entity.class.isAssignableFrom(type); type = type
				.getSuperclass()) {
			tempTypes.addLast(type.getName());
		}
		if (state != State.DELETED)
			writePropertyDirectly(TYPES, tempTypes);
		typesInitialized = true;
		return tempTypes;
	}

	public <T> T readProperty(Class<T> propertyType, String propertyName) {
		LOGGER.debug(String.format("read property : %s in %s", propertyName,
				getClass()));
		checkGlobalSanity(propertyName, Action.READ);
		if (!cache.keySet().contains(propertyName)) {
			// isEmbedded
			if (metadata().getPropertyAnnotation(propertyName,
					BackReference.class) == null) {
				cache.put(propertyName, resolve(propertyType, propertyName));
			} else {
				cache.put(
						propertyName,
						resolvePropertyByBackReference(propertyType,
								propertyName));
			}
		}
		return propertyType.cast(cache.get(propertyName));
	}

	@SuppressWarnings("unchecked")
	public <T> Collection<T> readCollection(Class<T> collectionGenericType,
			String collectionName) {
		LOGGER.debug(String.format("read collection : %s in %s",
				collectionName, getClass()));
		checkGlobalSanity(collectionName, Action.READ);
		if (!cache.containsKey(collectionName)) {
			cache.put(collectionName,
					resolveCollection(collectionGenericType, collectionName));
		}
		return (Collection<T>) cache.get(collectionName);
	}

	@Override
	public <T> Boolean add(String collectionName, T element,
			Class<T> collectionType) {
		checkSessionSanity(collectionName, element);
		return modifyCollection(collectionName, element, collectionType,
				Action.ADD);
	}

	@Override
	public <T> Boolean remove(String collectionName, T element,
			Class<T> collectionType) {
		return modifyCollection(collectionName, element, collectionType,
				Action.REMOVE);
	}

	protected <T> Boolean modifyCollection(String collectionName, T element,
			Class<T> collectionType, Action action) {
		checkNotNull(element);
		LOGGER.debug(String.format("%s %s %s %s in %s", action, element,
				action == Action.READ ? "from" : "to", collectionName,
				getClass()));
		Collection<T> collection = readCollection(collectionType,
				collectionName);
		boolean result = false;
		switch (action) {
		case ADD:
			result = collection.add(element);
			break;
		case REMOVE:
			result = collection.remove(element);
			break;
		default:
			throw new IllegalArgumentException(action.toString());
		}
		if (element instanceof Entity) {
			manageDependency(action, (Entity) element, collectionName);
		}
		writeProperty(collectionName, collection);
		return result;
	}

	private void manageDependency(Action action, Entity entity,
			String collectionName) {
		String backReferencePropertyName = metadata().getBackReference(
				collectionName);
		if (action == Action.ADD) {
			entity.attributeSession(getSession());
		}
		entity.writeProperty(backReferencePropertyName,
				action == Action.REMOVE ? null : this);
	}

	public void writeProperty(String propertyName, Object value) {
		checkSessionSanity(propertyName, value);
		LOGGER.debug(String.format("write %s in %s in %s", value, propertyName,
				getClass()));
		if (value != null && value instanceof Entity
				&& ((Entity) value).getSession() == null) {
			((Entity) value).attributeSession(getSession());
		}
		checkGlobalSanity(propertyName, Action.WRITE);
		Object oldValue = cache.get(propertyName);
		cache.put(propertyName, value);
		if (write(propertyName, value) && !ID.equals(propertyName)) {
			LOGGER.debug("property changed");
			PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(
					this, propertyName, oldValue, value);
			eventBus.post(propertyChangeEvent);
		}
		if (cache.values().containsAll(entityMetadata.propertyNames())) {
			setCompletion(Completion.FULL);
		}
	}

	/**
	 * no {@link PropertyChangeEvent}
	 */
	public void writePropertyDirectly(String propertyName, Object value) {
		checkSessionSanity(propertyName, value);
		LOGGER.debug(String.format("write directly %s in %s in %s", value,
				propertyName, getClass()));
		if (value != null && value instanceof Entity
				&& ((Entity) value).getSession() == null) {
			((Entity) value).attributeSession(getSession());
		}
		checkGlobalSanity(propertyName, Action.WRITE);
		cache.put(propertyName, value);
		write(propertyName, value);
		if (cache.values().containsAll(entityMetadata.propertyNames())) {
			setCompletion(Completion.FULL);
		}
	}

	public Boolean hasFilledProperty(String propertyName) {
		return cache.keySet().contains(propertyName);
	}

	@JsonIgnore
	@Override
	public State getState() {
		return this.state;
	}

	@Override
	public void setState(State state) {
		checkChange(this.state, state);
		this.state = state;
	}

	private void checkChange(State oldState, State newState) {
		if (oldState == State.DELETED
				|| (oldState == State.MODIFIED && newState == State.NEW)) {
			throw new StateChangeException(oldState, newState);
		}

	}

	@Override
	public List<String> getTypes() {
		if (!typesInitialized) {
			initTypes();
		}
		return ImmutableList.copyOf(readCollection(String.class, TYPES));
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
	@JsonIgnore
	public Completion getCompletion() {
		return this.completion;
	}

	@Override
	@JsonIgnore
	public void setCompletion(Completion completion) {
		this.completion = completion;
	}

	protected void checkGlobalSanity(String propertyName, Action action) {
		checkArgument(entityMetadata.propertyNames().contains(propertyName),
				"La propriété %s n'est pas connu pour les objets de type %s",
				propertyName, getClass());
	}

	@SuppressWarnings("unchecked")
	protected void checkSessionSanity(String propertyName, Object o) {
		if (o == null) {
			return;
		}
		if (!Entity.class.isAssignableFrom(o.getClass())) {
			if (!Collection.class.isAssignableFrom(o.getClass()))
				return;
			Class<?> parameterType = entityMetadata
					.propertyParameterClasses(propertyName)[0];
			if (!Entity.class.isAssignableFrom(parameterType))
				return;
			Collection<? extends Entity> col = (Collection<? extends Entity>) o;
			for (Entity e : col) {
				checkSessionSanity(propertyName, e);
				return;
			}
			return;
		}
		Entity entity = (Entity) o;
		if (Objects.equal(getSession(), entity.getSession())) {
			return;
		}
		if (entity.getState() == State.COMMITED || entity.getSession() == null) {
			return;
		}
		throw new IllegalArgumentException(
				"Only entities from same session or in state COMMITED can be added");
	}

	@InView(GenericEmbedingViews.Id.class)
	@JsonProperty("_id")
	public String getId() {
		String id = readProperty(String.class, ID);
		if (id == null) {
			init();
			return getId();
		}
		return id;
	}

	protected void init() {
		state = State.NEW;
		writeProperty(ID, tempId);
		initTypes();
	}

	@JsonProperty("_rev")
	public String getRev() {
		return readProperty(String.class, REV);
	}

	@JsonProperty("_rev")
	public void setRev(String rev) {
		writePropertyDirectly(REV, rev);
	}

	@JsonIgnore
	@Override
	public EntityMetadata metadata() {
		return entityMetadata;
	}

	@JsonIgnore
	@Override
	public Session getSession() {
		return session;
	}

	/**
	 * seté la {@link Session} d'une {@link Entity} enregistre cette session
	 * comme listener de l'entité et supprime la session précédente des listener
	 */
	@Override
	public void attributeSession(Session session) {
		if (Objects.equal(this.session, session)) {
			return;
		}
		checkState(
				this.state == State.COMMITED || this.session == null,
				"can only change session if commited (actual state %s) or session is null %s",
				this.state, session);
		checkNotNull(session, "session may not be null");
		if (this.session != null) {
			unregisterListener(this.session);
		}
		this.session = session;
		registerListener(session);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().isAssignableFrom(obj.getClass())) {
			return false;
		}
		Entity entity = Entity.class.cast(obj);
		return Objects.equal(this.getId(), entity.getId());
	}

	@Override
	public String toString() {
		return getId();
	}
}
