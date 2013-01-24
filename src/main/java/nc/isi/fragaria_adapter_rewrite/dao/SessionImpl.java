package nc.isi.fragaria_adapter_rewrite.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import nc.isi.fragaria_adapter_rewrite.dao.adapters.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;
import nc.isi.fragaria_adapter_rewrite.enums.Completion;
import nc.isi.fragaria_adapter_rewrite.enums.State;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;

public class SessionImpl implements Session {
	private final AdapterManager adapterManager;
	private final UUID id = UUID.randomUUID();
	private final EntityBuilder entityBuilder;

	private final LinkedList<Entity> queue = Lists.newLinkedList();

	private final LinkedListMultimap<Class<? extends Entity>, Entity> createdObjects = LinkedListMultimap
			.create();
	private final LinkedListMultimap<Class<? extends Entity>, Entity> deletedObjects = LinkedListMultimap
			.create();
	private final LinkedListMultimap<Class<? extends Entity>, Entity> updatedObjects = LinkedListMultimap
			.create();
	@SuppressWarnings("unchecked")
	private final LinkedListMultimap<Class<? extends Entity>, Entity>[] caches = new LinkedListMultimap[] {
			createdObjects, deletedObjects, updatedObjects };

	public SessionImpl(AdapterManager adapterManager,
			EntityBuilder entityBuilder) {
		this.adapterManager = adapterManager;
		this.entityBuilder = entityBuilder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Entity> Collection<T> get(Query<T> query) {
		CollectionQueryResponse<T> response = (CollectionQueryResponse<T>) adapterManager
				.executeQuery(query);
		Collection<T> objects = response.getResponse();
		Class<T> entityClass = query.getResultType();
		objects.addAll((Collection<T>) createdObjects.get(entityClass));
		objects.removeAll(deletedObjects.get(entityClass));
		for (T o : (Collection<T>) updatedObjects.get(entityClass)) {
			objects.remove(o);
			objects.add(o);
		}
		changeSession(objects);
		return objects;
	}

	@Override
	public <T extends Entity> T getUnique(Query<T> query) {
		T entity = adapterManager.executeUniqueQuery(query).getResponse();
		T cachedValue = getRegisteredValue(entity);
		if (cachedValue != null)
			entity = cachedValue;
		changeSession(entity);
		return entity;
	}

	@SuppressWarnings("unchecked")
	public <T extends Entity> T getRegisteredValue(T entity) {
		for (LinkedListMultimap<Class<? extends Entity>, Entity> cache : caches) {
			if (isRegistered(entity, cache))
				return (T) cache.get(entity.getClass()).get(
						cache.get(entity.getClass()).indexOf(entity));
		}
		return null;
	}

	@Override
	public <T extends Entity> T create(Class<T> entityClass) {
		T entity = entityBuilder.build(entityClass);
		entity.setState(State.NEW);
		entity.setCompletion(Completion.FULL);
		entity.setSession(this);
		register(entity, createdObjects);
		return entity;
	}

	@Override
	public void delete(Entity... entities) {
		delete(Arrays.asList(entities));
	}

	@Override
	public <T extends Entity> void delete(Collection<T> entities) {
		checkNotDeleted(entities);
		for (T entity : entities) {
			if (isRegistered(entity, createdObjects))
				createdObjects.remove(entity.getClass(), entity);
			if (isRegistered(entity, updatedObjects))
				updatedObjects.remove(entity.getClass(), entity);
			register(entity, deletedObjects);
			entity.setState(State.DELETED);
		}
	}

	private <T extends Entity> void checkNotDeleted(Collection<T> entities) {
		checkNotNull(entities);
		for (T entity : entities) {
			checkState(entity.getState() != State.DELETED);
		}
	}

	/**
	 * 
	 * @param e
	 */
	@Subscribe
	public void recordPropertyChange(PropertyChangeEvent e) {
		Entity entity = (Entity) e.getSource();
		entity.setState(State.MODIFIED);
		if (isRegistered(entity, deletedObjects))
			commitError(entity, entity.getState(), State.DELETED);
		register(entity, isRegistered(entity, createdObjects) ? createdObjects
				: updatedObjects);
	}

	@Override
	public Session post() {
		adapterManager.post(queue);
		return renewSession();
	}

	protected Session renewSession() {
		for (Multimap<Class<? extends Entity>, Entity> cache : caches)
			cache.clear();
		queue.clear();
		return this;
	}

	@Override
	public Session cancel() {
		return renewSession();
	}

	/**
	 * 
	 * @param object
	 * 
	 *            Ajoute les objets à une file (queue). Ces objets seront
	 *            ensuite traitées par les {@link AdapterManager}
	 */
	protected <T extends Entity> void register(T entity,
			Multimap<Class<? extends Entity>, Entity> map) {
		map.put(entity.getClass(), entity);
		queue.add(entity);
	}

	private void commitError(Entity entity, State oldState, State state) {
		throw new RuntimeException(
				String.format(
						"Erreur sur l'état de l'objet %s, déjà enregistré avec l'état %s et demande à passer à %s ",
						entity, oldState, state));
	}

	private Boolean isRegistered(Entity entity,
			Multimap<Class<? extends Entity>, Entity> map) {
		return map.containsValue(entity);
	}

	public <T extends Entity> void changeSession(Collection<T> entities) {
		for (T entity : entities) {
			entity.setSession(this);
		}
	}

	public void changeSession(Entity... entities) {
		changeSession(Arrays.asList(entities));
	}

	public UUID getId() {
		return id;
	}

}
