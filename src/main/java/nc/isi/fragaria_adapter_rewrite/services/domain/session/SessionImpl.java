package nc.isi.fragaria_adapter_rewrite.services.domain.session;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import nc.isi.fragaria_adapter_rewrite.services.domain.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.services.domain.CollectionQueryResponse;
import nc.isi.fragaria_adapter_rewrite.services.domain.Completion;
import nc.isi.fragaria_adapter_rewrite.services.domain.Entity;
import nc.isi.fragaria_adapter_rewrite.services.domain.EntityBuilder;
import nc.isi.fragaria_adapter_rewrite.services.domain.OperationType;
import nc.isi.fragaria_adapter_rewrite.services.domain.Query;
import nc.isi.fragaria_adapter_rewrite.services.domain.State;
import nc.isi.fragaria_adapter_rewrite.services.domain.UniqueQueryResponse;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;

public class SessionImpl implements Session {

	private final AdapterManager adapterManager;

	private final QueryExecutorForCollection qExecutor;
	private final UUID id = UUID.randomUUID();
	private final Session parent;
	private final Collection<Session> childs = Lists.newArrayList();
	private final EntityBuilder entityBuilder;
	
	private final LinkedList<Entity> queue = Lists.newLinkedList();

	private final Multimap<Class<Entity>, Entity> createdObjects = LinkedListMultimap
			.create();
	private final Multimap<Class<Entity>, Entity> deletedObjects = LinkedListMultimap
			.create();
	private final Multimap<Class<Entity>, Entity> updatedObjects = LinkedListMultimap
			.create();

	public SessionImpl(AdapterManager adapterManager,
			EntityBuilder entityBuilder,QueryExecutorForCollection qExecutor) {
		this.parent = null;
		this.adapterManager = adapterManager;
		this.entityBuilder = entityBuilder;
		this.qExecutor = qExecutor;

	}

	public SessionImpl(SessionImpl parent) {
		this.parent = parent;
		this.adapterManager = parent.getAdapterManager();
		this.entityBuilder = parent.getEntityBuilder();
		this.qExecutor = parent.getqExecutor();
	}

	@Override
	public Collection<Entity> get(Query<Entity> query) {
		Collection<Entity> objects;
//		if (parent != null)
//			objects = parent.get(query);
//		else {
			CollectionQueryResponse<Entity> response = (CollectionQueryResponse<Entity>) adapterManager
					.executeQuery(query);
			objects = (Collection<Entity>) response.getResponse();
//		}

		objects.addAll((Collection<Entity>) createdObjects.get((Class<Entity>) objects.getClass()
				.getComponentType()));
		
		objects.removeAll(deletedObjects.get((Class<Entity>) objects.getClass()
				.getComponentType()));
		
		for (Object o : updatedObjects.get((Class<Entity>) objects.getClass()
				.getComponentType())) {
			objects.remove(o);
			objects.add((Entity) o);
		}
		
		setSessionTo(objects);
		return objects;
	}


	

	@Override
	public Entity getUnique(Query<Entity> query) {
		Entity entity;
		if(query!=null)
			entity = getObjectFromCacheFor(query);
//		if (entity == null) {
//			if (parent != null)
//				entity = parent.getUnique(query);
//			else {
				UniqueQueryResponse<Entity> response = (UniqueQueryResponse<Entity>) adapterManager
						.executeQuery(query);
				entity = response.getResponse();
//			}
//		}
		setSessionTo(entity);
		return entity;
	}

	@Override
	public Entity create(Class<Entity> entityClass) {
		Entity entity = entityBuilder.build(entityClass);
		entity.setCompletion(Completion.FULL);
		register(OperationType.CREATE, entity);
		return entity;
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
	public Session post() {
		adapterManager.post(queue);
		flush();
		return null;
	}

	@Override
	public Session cancel() {
		flush();
		return this;
	}


	@Subscribe
	@Override
	public void register(OperationType o, Entity entity) {
		switch (o) {
		case CREATE:
			entity.setState(State.NEW);
			if (!createdObjects.values().contains(entity))
				createdObjects.put((Class<Entity>) entity.getClass(), entity);
			if (deletedObjects.values().contains(entity))
				deletedObjects.remove(entity.getClass(), entity);
			break;
		case UPDATE:
			if(entity.getState()!=State.NEW)
				entity.setState(State.MODIFIED);
			if (createdObjects.values().contains(entity))
				break;
			if (deletedObjects.values().contains(entity))
				throw new RuntimeException(
						"impossible d'updater un objet deleté");
			if (!createdObjects.values().contains(entity)
					&& !updatedObjects.values().contains(entity))
				updatedObjects.put((Class<Entity>) entity.getClass(), entity);
			break;
		case DELETE:
			entity.setState(State.DELETED);
			if (createdObjects.values().contains(entity))
				createdObjects.remove(entity.getClass(), entity);
			if (updatedObjects.values().contains(entity))
				updatedObjects.remove(entity.getClass(), entity);
			if (deletedObjects.values().contains(entity))
				break;
			if (!deletedObjects.values().contains(entity))
				deletedObjects.put((Class<Entity>) entity.getClass(), entity);
			break;
		default:
			break;
		}
		setSessionTo(entity);
		queue.add(entity);
	}

	private void setSessionTo(Collection<Entity> entities) {
		for(Entity entity : entities){
			entity.setSession(this);
		}
	}
	
	private void setSessionTo(Entity...entities) {
		setSessionTo(Arrays.asList(entities));
	}
	
	private Entity getObjectFromCacheFor(Query<Entity> query) {
		Entity object = null;
		
		object = (Entity)qExecutor.getUniqueObjectFromEntityCollFor(
				query,(Collection<Entity>)updatedObjects.get(query.getResultType()));
		if (object == null)
			object = (Entity)qExecutor.getUniqueObjectFromEntityCollFor(
					query,(Collection<Entity>)createdObjects.get(query.getResultType()));
		else if (qExecutor.getUniqueObjectFromEntityCollFor(
				query,(Collection<Entity>)deletedObjects.get(query.getResultType())) != null)
			throw new RuntimeException("Impossible de getter un objet déleté");
		
		return object;
	}
	
	private void flush() {
		createdObjects.clear();
		updatedObjects.clear();
		deletedObjects.clear();
		queue.clear();
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


	public AdapterManager getAdapterManager() {
		return adapterManager;
	}
	
	public EntityBuilder getEntityBuilder() {
		return entityBuilder;
	}
	
	public QueryExecutorForCollection getqExecutor() {
		return qExecutor;
	}
	

}
