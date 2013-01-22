package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class SessionImpl implements Session {

	private final AdapterManager adapterManager;
	private final UUID id = UUID.randomUUID();
	private final Session parent;
	private final Collection<Session> childs = Lists.newArrayList();
	private final EntityBuilder entityBuilder;

	private final Multimap<Class<?>, Object> createdObjects = LinkedListMultimap
			.create();
	private final Multimap<Class<?>, Object> deletedObjects = LinkedListMultimap
			.create();;
	private final Multimap<Class<?>, Object> updatedObjects = LinkedListMultimap
			.create();

	public SessionImpl(AdapterManager adapterManager,
			EntityBuilder entityBuilder) {
		this.parent = null;
		this.adapterManager = adapterManager;
		this.entityBuilder = entityBuilder;

	}

	public SessionImpl(Session parent) {
		this.parent = parent;
		this.adapterManager = parent.getAdapterManager();
		this.entityBuilder = parent.getEntityBuilder();
	}

	@Override
	public <T extends Entity> Collection<T> get(Query<T> query) {
		Collection<T> objects;
		if (parent != null)
			objects = parent.get(query);
		else {
			CollectionQueryResponse<T> response = (CollectionQueryResponse<T>) adapterManager
					.executeQuery(query);
			objects = (Collection<T>) response.getResponse();
		}

		objects.addAll((Collection<T>) createdObjects.get(objects.getClass()
				.getComponentType()));
		objects.removeAll(deletedObjects.get(objects.getClass()
				.getComponentType()));
		for (Object o : updatedObjects.get(objects.getClass()
				.getComponentType())) {
			objects.remove(o);
			objects.add((T) o);
		}
		return objects;
	}

	@Override
	public <T extends Entity> T getUnique(Query<T> query) {
		T object = getObjectFromCacheFor(query);
		if (object == null) {
			if (parent != null)
				object = parent.getUnique(query);
			else {
				UniqueQueryResponse<T> response = (UniqueQueryResponse<T>) adapterManager
						.executeQuery(query);
				object = response.getResponse();
			}
		}
		return object;
	}

	@Override
	public <T extends Entity> T create(Class<T> entityClass) {
		T object = entityBuilder.build(entityClass);
		object.setCompletion(Completion.FULL);
		return object;
	}

	@Override
	public Session post() {
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.addAll((Collection<? extends Entity>) createdObjects.values());
		entities.addAll((Collection<? extends Entity>) updatedObjects.values());
		entities.addAll((Collection<? extends Entity>) deletedObjects.values());
//		adapterManager.post(entities);
		flush();
		return null;
	}

	@Override
	public Session cancel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Entity... entities) {
		delete(Arrays.asList(entities));
	}

	@Override
	public void delete(Collection<Entity> entities) {
		for (Entity entity : entities) {
			register(OperationType.DELETE, entity);
		}

	}

	@Override
	public void register(OperationType o, Object object) {
		switch (o) {
		case CREATE:
			if (!createdObjects.values().contains(object))
				createdObjects.put(object.getClass(), object);
			if (deletedObjects.values().contains(object))
				deletedObjects.remove(object.getClass(), object);
			break;
		case UPDATE:
			if (createdObjects.values().contains(object))
				break;
			if (deletedObjects.values().contains(object))
				throw new RuntimeException(
						"impossible d'updater un objet deleté");
			if (!createdObjects.values().contains(object)
					&& !updatedObjects.values().contains(object))
				updatedObjects.put(object.getClass(), object);
			break;
		case DELETE:
			if (createdObjects.values().contains(object))
				createdObjects.remove(object.getClass(), object);
			if (updatedObjects.values().contains(object))
				updatedObjects.remove(object.getClass(), object);
			if (deletedObjects.values().contains(object))
				break;
			if (!deletedObjects.values().contains(object))
				deletedObjects.put(object.getClass(), object);
			break;
		default:
			break;
		}

	}

	@Override
	public Session createChild() {
		Session session = new SessionImpl(this);
		addChild(session);
		return session;
	}

	public UUID getId() {
		return id;
	}

	public void addChild(Session session) {
		if (parent != null)
			parent.addChild(session);
		childs.add(session);
	}

	@Override
	public AdapterManager getAdapterManager() {
		return adapterManager;
	}

	private <T extends Entity> T getObjectFromCacheFor(Query<T> query) {
		T object = null;

//		object = (T) getUniqueObjectFromCollFor(
//				(Collection<T>) updatedObjects.get(query.getType()), query);
//		if (object == null)
//			object = (T) getUniqueObjectFromCollFor(
//					(Collection<T>) createdObjects.get(query.getType()), query);
//		else if (getUniqueObjectFromCollFor(
//				(Collection<T>) deletedObjects.get(query.getType()), query) != null)
//			throw new RuntimeException("Impossible de getter un objet deleté");
//
//		if (object == null)
//			if (parent != null)
//				object = parent.getUnique(query);
//			else {
//				UniqueQueryResponse<T> response = (UniqueQueryResponse<T>) adapterManager
//						.executeQuery(query);
//				object = (T) response.getResponse();
//			}

		return object;
	}

	private <T extends Entity> T getUniqueObjectFromCollFor(Collection<T> coll,
			Query<T> query) {
				return null;
//		T t = alias(query.getType(), query.getType().getSimpleName());
//		T object = from($(t), coll).where(query.getPredicate()).uniqueResult(
//				$(t));
//		return object;
	}

	public EntityBuilder getEntityBuilder() {
		return entityBuilder;
	}

	private void flush() {
		createdObjects.clear();
		updatedObjects.clear();
		deletedObjects.clear();
	}

}
