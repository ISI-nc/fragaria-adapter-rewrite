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

import nc.isi.fragaria_adapter_rewrite.annotations.InView;
import nc.isi.fragaria_adapter_rewrite.dao.Session;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericEmbedingViews;
import nc.isi.fragaria_adapter_rewrite.entities.views.View;
import nc.isi.fragaria_adapter_rewrite.enums.Completion;
import nc.isi.fragaria_adapter_rewrite.enums.State;
import nc.isi.fragaria_adapter_rewrite.exceptions.StateChangeException;
import nc.isi.fragaria_adapter_rewrite.services.ObjectMapperProvider;
import nc.isi.fragaria_adapter_rewrite.services.TapestryRegistry;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;
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

	private final ObjectNode objectNode;
	private final ObjectResolver objectResolver;
	private final Map<String, Object> cache = Maps.newHashMap();
	private final EntityMetadata entityMetadata;
	private final EventBus eventBus = new EventBus();
	private final List<String> types;
	private State state = State.COMMITED;
	private Completion completion = Completion.PARTIAL;
	private Session session;

	public AbstractEntity() {
		this(TapestryRegistry.INSTANCE.getRegistry()
				.getService(ObjectMapperProvider.class).provide()
				.createObjectNode(), TapestryRegistry.INSTANCE.getRegistry()
				.getService(ObjectResolver.class), TapestryRegistry.INSTANCE
				.getRegistry().getService(EntityMetadataFactory.class));
		init();
	}

	public AbstractEntity(ObjectNode objectNode, ObjectResolver objectResolver,
			EntityMetadataFactory entityMetadataFactory) {
		this.objectNode = checkNotNull(objectNode);
		this.objectResolver = objectResolver;
		this.entityMetadata = entityMetadataFactory.create(getClass());
		this.types = initTypes();
		init();
	}

	/**
	 * do your initialization here with init(property, value)
	 */
	protected void init() {
		if (getId() == null) {
			writeProperty(ID, UUID.randomUUID().toString());
			setState(State.NEW);
		}
	}

	private List<String> initTypes() {
		LinkedList<String> tempTypes = new LinkedList<>();
		for (Class<?> type = getClass(); Entity.class.isAssignableFrom(type); type = type
				.getSuperclass()) {
			tempTypes.addLast(type.getName());
		}
		objectResolver.write(objectNode, TYPES, tempTypes, this);
		return tempTypes;
	}

	@Override
	public ObjectNode toJSON() {
		return objectNode.deepCopy();
	}

	protected <T> T readProperty(Class<T> propertyType, String propertyName) {
		LOGGER.debug(String.format("read property : %s in %s", propertyName,
				getClass()));
		checkGlobalSanity(propertyName, Action.READ);
		if (!cache.keySet().contains(propertyName)) {
			cache.put(propertyName, objectResolver.resolve(objectNode,
					propertyType, propertyName, this));
		}
		T value = propertyType.cast(cache.get(propertyName));
		if (Entity.class.isAssignableFrom(propertyType)) {
			((Entity) value).setSession(session);
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	protected <T> Collection<T> readCollection(Class<T> collectionGenericType,
			String collectionName) {
		LOGGER.debug(String.format("read collection : %s in %s",
				collectionName, getClass()));
		checkGlobalSanity(collectionName, Action.READ);
		if (!cache.containsKey(collectionName)) {
			cache.put(collectionName, objectResolver.resolveCollection(
					objectNode, collectionGenericType, collectionName, this));
		}
		Collection<T> collection = (Collection<T>) cache.get(collectionName);
		if (Entity.class.isAssignableFrom(collectionGenericType)) {
			for (T o : collection) {
				((Entity) o).setSession(session);
			}
		}
		return collection;
	}

	protected <T> Boolean add(String collectionName, T element,
			Class<T> collectionType) {
		return modifyCollection(collectionName, element, collectionType,
				Action.ADD);
	}

	protected <T> Boolean remove(String collectionName, T element,
			Class<T> collectionType) {
		return modifyCollection(collectionName, element, collectionType,
				Action.REMOVE);
	}

	protected <T> Boolean modifyCollection(String collectionName, T element,
			Class<T> collectionType, Action action) {
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
		writeProperty(collectionName, collection);
		return result;
	}

	protected void writeProperty(String propertyName, Object value) {
		LOGGER.debug(String.format("write %s in %s in %s", value, propertyName,
				getClass()));
		checkGlobalSanity(propertyName, Action.WRITE);
		Object oldValue = cache.get(propertyName);
		objectResolver.write(objectNode, propertyName, value, this);
		cache.put(propertyName, value);
		PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this,
				propertyName, oldValue, value);
		eventBus.post(propertyChangeEvent);
		if (cache.values().containsAll(entityMetadata.propertyNames())) {
			setCompletion(Completion.FULL);
		}
	}

	/**
	 * La même chose que write mais seulement si la propriété est vide
	 * 
	 * @param propertyName
	 * @param value
	 */
	protected void init(String propertyName, Object value) {
		LOGGER.debug(String.format("init %s in %s in %s", value, propertyName,
				getClass()));
		if (readProperty(value.getClass(), propertyName) != null) {
			writeProperty(propertyName, value);
			LOGGER.debug(String.format("init completed"));
		} else {
			LOGGER.debug(String.format("no initialization required"));
		}
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
		if (oldState == State.DELETED
				|| (oldState == State.MODIFIED && newSate == State.NEW)) {
			throw new StateChangeException(oldState, newSate);
		}

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
		if (action != Action.READ) {
			checkState(state != State.DELETED,
					"impossible de %s les propriétés d'un objet effacé", action);
		}
		checkArgument(entityMetadata.propertyNames().contains(propertyName),
				"La propriété %s n'est pas connu pour les objets de type %s",
				propertyName, getClass());
	}

	@InView(GenericEmbedingViews.Id.class)
	@JsonProperty("_id")
	public String getId() {
		return readProperty(String.class, ID);
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
		if (Objects.equal(this.session, session))
			return;
		if (this.session != null) {
			unregisterListener(this.session);
		}
		this.session = session;
		if (session != null) {
			registerListener(session);
		}
	}

	@Override
	public ObjectNode toJSON(Class<? extends View> view) {
		return objectResolver.clone(objectNode, view, this);
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
		AbstractEntity entity = AbstractEntity.class.cast(obj);
		return Objects.equal(this.getId(), entity.getId());
	}

}
