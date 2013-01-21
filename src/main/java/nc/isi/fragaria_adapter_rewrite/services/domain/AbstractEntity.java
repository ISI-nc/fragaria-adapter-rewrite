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

	public AbstractEntity(ObjectNode objectNode, ObjectResolver objectResolver,
			EntityMetadataFactory entityMetadataFactory) {
		this.objectNode = checkNotNull(objectNode);
		this.objectResolver = objectResolver;
		this.entityMetadata = entityMetadataFactory.create(getClass());
		types = initTypes();
		setId(UUID.randomUUID());
	}

	private LinkedList<String> initTypes() {
		LinkedList<String> types = new LinkedList<>();
		if (state == State.NEW) {
			Collection<String> loadedTypes = readCollection(String.class, TYPES);
			if (loadedTypes != null) {
				types.addAll(loadedTypes);
				return types;
			}
			for (Class<?> type = getClass(); Entity.class
					.isAssignableFrom(type); type = type.getSuperclass()) {
				types.addLast(type.getName());
			}
			writeProperty(TYPES, types);
		}
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
		Class<? extends View> view = entityMetadata.getEmbeded(propertyName);
		if (view != null)
			objectResolver.write(objectNode, propertyName, value, view);
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
	public Completion getCompletion() {
		return this.completion;
	}

	@Override
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
	public UUID getId() {
		return readProperty(UUID.class, ID);
	}

	public void setId(UUID id) {
		writeProperty(ID, id);
	}

	@JsonView(Id.class)
	public UUID getRev() {
		return readProperty(UUID.class, REV);
	}

	public void setRev(UUID id) {
		writeProperty(REV, id);
	}

	@Override
	public EntityMetadata getMetadata() {
		return entityMetadata;
	}

}
