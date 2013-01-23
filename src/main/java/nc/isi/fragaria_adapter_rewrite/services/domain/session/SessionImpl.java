package nc.isi.fragaria_adapter_rewrite.services.domain.session;

import java.beans.PropertyChangeEvent;
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
	private final EntityBuilder entityBuilder;

	private final LinkedList<Entity> queue = Lists.newLinkedList();

	private final Multimap<Class<? extends Entity>, Entity> createdObjects = LinkedListMultimap
			.create();
	private final Multimap<Class<? extends Entity>, Entity> deletedObjects = LinkedListMultimap
			.create();
	private final Multimap<Class<? extends Entity>, Entity> updatedObjects = LinkedListMultimap
			.create();

	public SessionImpl(AdapterManager adapterManager,
			EntityBuilder entityBuilder, QueryExecutorForCollection qExecutor) {
		this.adapterManager = adapterManager;
		this.entityBuilder = entityBuilder;
		this.qExecutor = qExecutor;
	}

	@Override
	public <T extends Entity> Collection<T> get(Query<T> query) {
		CollectionQueryResponse<T> response = (CollectionQueryResponse<T>) adapterManager
				.executeQuery(query);
		Collection<T>  objects = (Collection<T>) response.getResponse();
		Class<T> entityClass = (Class<T>) objects.getClass().getComponentType();
		objects.addAll((Collection<T>) createdObjects.get(entityClass));
		objects.removeAll(deletedObjects.get(entityClass));
		for (T o : (Collection<T>)updatedObjects.get(entityClass)) {
			objects.remove(o);
			objects.add((T) o);
		}
		setSessionTo(objects);
		return objects;
	}

	@Override
	public <T extends Entity> T getUnique(Query<T> query) {
		T entity = getObjectFromCacheFor(query);
		if (entity == null) {
			UniqueQueryResponse<T> response = 
					(UniqueQueryResponse<T>) adapterManager.executeUniqueQuery(query);
			entity = response.getResponse();
		}
		setSessionTo(entity);
		return entity;
	}

	@Override
	public <T extends Entity> T create(Class<T> entityClass) {
		T entity = entityBuilder.build(entityClass);
		entity.setState(State.NEW);
		entity.setCompletion(Completion.FULL);
		register(OperationType.CREATE, entity);
		return entity;
	}

	@Override
	public void delete(Entity... entities) {
		delete(Arrays.asList(entities));
	}

	@Override
	public <T extends Entity> void delete(Collection<T> entities) {
		for (T entity : entities) {
			entity.setState(State.DELETED);
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

	@Override
	public <T extends Entity> void register(OperationType o, T entity) {
		setSessionTo(entity);
		queue.add(entity);

		switch (o) {
		case CREATE:
				createdObjects.put(entity.getClass(), entity);
			break;
		case UPDATE:
			if(getMapIfObjectHasBeenRegistered(entity)==null)
				updatedObjects.put(entity.getClass(), entity);
			else if(getMapIfObjectHasBeenRegistered(entity) == createdObjects)
				break;
			else if(getMapIfObjectHasBeenRegistered(entity) == updatedObjects) 
				break;
			else if(getMapIfObjectHasBeenRegistered(entity) == deletedObjects) 
				commitError(entity,entity.getState(),  State.DELETED);
			break;
		case DELETE:
			if(getMapIfObjectHasBeenRegistered(entity)==null)
				deletedObjects.put(entity.getClass(), entity);
			else if(getMapIfObjectHasBeenRegistered(entity) == createdObjects)
				createdObjects.remove(entity.getClass(), entity);
			else if(getMapIfObjectHasBeenRegistered(entity) == updatedObjects) {
				updatedObjects.remove(entity.getClass(), entity);
				deletedObjects.put(entity.getClass(), entity);
			}
			else if(getMapIfObjectHasBeenRegistered(entity) == deletedObjects) 
				break;
			break;
		default:
			break;
		}

	}
		
	private void commitError(Entity entity, State oldState, State state) {
		throw new RuntimeException(
				String.format(
						"Erreur sur l'état de l'objet %s, déjà enregistré avec l'état %s et demande à passer à %s ",
						entity, oldState, state));
	}
	
	private <T extends Entity> Multimap<Class<? extends Entity>, Entity> getMapIfObjectHasBeenRegistered(T entity){
		if(deletedObjects.get(entity.getClass()).contains(entity))
			return deletedObjects;
		if(createdObjects.get(entity.getClass()).contains(entity))
			return createdObjects;
		if(updatedObjects.get(entity.getClass()).contains(entity))
			return deletedObjects;
		return null;
	}
	
	
    @Subscribe void recordPropertyChange(PropertyChangeEvent e) {
    	register(OperationType.UPDATE, (Entity) e.getSource());
      }

    private <T extends Entity> void setSessionTo(Collection<T> entities) {
		for(T entity : entities){
			entity.setSession(this);
		}
	}

	private void setSessionTo(Entity... entities) {
		setSessionTo(Arrays.asList(entities));
	}

	private <T extends Entity> T getObjectFromCacheFor(Query<T> query) {
		T entity = null;
		if(deletedObjects.size()>0)
			if (getEntityFromColl(query,(Collection<T>)deletedObjects.get(query.getResultType())) != null)
				throw new RuntimeException("Impossible de getter un objet déleté");
		if(updatedObjects.size()>0)
			entity = getEntityFromColl(query, (Collection<T>)updatedObjects.get(query.getResultType()));
		if (entity == null && createdObjects.size()>0)
			entity = getEntityFromColl(query, (Collection<T>)createdObjects.get(query.getResultType()));
		return entity;
	}
	
	private <T extends Entity> T getEntityFromColl(Query<T> query,Collection<T> coll) {
		T entity = null;
		if(updatedObjects.size()>0)
			entity = qExecutor.getUniqueObjectFromEntityCollFor(
					query,coll);
		return entity;
	}

	private void flush() {
		createdObjects.clear();
		updatedObjects.clear();
		deletedObjects.clear();
		queue.clear();
	}


	public UUID getId() {
		return id;
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
