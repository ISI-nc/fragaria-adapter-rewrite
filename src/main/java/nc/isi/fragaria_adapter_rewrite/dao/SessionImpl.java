package nc.isi.fragaria_adapter_rewrite.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.mysema.query.alias.Alias.$;
import static com.mysema.query.alias.Alias.alias;
import static com.mysema.query.collections.MiniApi.from;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import nc.isi.fragaria_adapter_rewrite.dao.adapters.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;
import nc.isi.fragaria_adapter_rewrite.enums.State;

import org.apache.log4j.Logger;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.Ops;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.PathBuilder;

public class SessionImpl implements Session {
	private static final Logger LOGGER = Logger.getLogger(SessionImpl.class);
	private final AdapterManager adapterManager;
	private final UUID id = UUID.randomUUID();
	private final EntityBuilder entityBuilder;

	private final List<Entity> queue = Lists.newLinkedList();

	private final HashMultimap<Class<? extends Entity>, Entity> createdObjects = HashMultimap
			.create();
	private final HashMultimap<Class<? extends Entity>, Entity> deletedObjects = HashMultimap
			.create();
	private final HashMultimap<Class<? extends Entity>, Entity> updatedObjects = HashMultimap
			.create();
	@SuppressWarnings("unchecked")
	private final HashMultimap<Class<? extends Entity>, Entity>[] caches = new HashMultimap[] {
			createdObjects, deletedObjects, updatedObjects };

	public SessionImpl(AdapterManager adapterManager,
			EntityBuilder entityBuilder) {
		this.adapterManager = adapterManager;
		this.entityBuilder = entityBuilder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Entity> Collection<T> get(Query<T> query, boolean cache) {
		CollectionQueryResponse<T> response = (CollectionQueryResponse<T>) adapterManager
				.executeQuery(query);
		Collection<T> objects = response.getResponse();
		LOGGER.debug(String.format("list without cache : %s", objects));
		if (cache) {
			Class<T> entityClass = query.getResultType();
			objects.addAll((Collection<T>) createdObjects.get(entityClass));
			objects.removeAll(deletedObjects.get(entityClass));
			for (T o : (Collection<T>) updatedObjects.get(entityClass)) {
				objects.remove(o);
				objects.add(o);
			}
			LOGGER.debug(String.format("list after cache : %s", objects));
		}
		changeSession(objects);
		LOGGER.info(String.format("session %s get : %s", getId(), objects));
		return objects;
	}

	@Override
	public <T extends Entity> Collection<T> get(Query<T> query) {
		return get(query, true);
	}

	@Override
	public <T extends Entity> T getUnique(Query<T> query, boolean cache) {
		T entity = adapterManager.executeUniqueQuery(query).getResponse();
		if (cache) {
			T cachedValue = entity != null ? getRegisteredValue(entity)
					: findCachedValue(query);
			LOGGER.debug("cachedValue : " + cachedValue);
			if (cachedValue != null) {
				LOGGER.debug("was registered");
				entity = cachedValue;
			}
		}
		if (entity != null && entity.getSession() == null) {
			changeSession(entity);
		}
		LOGGER.info(String.format("session %s getUnique : %s", getId(), entity));
		return entity;
	}

	@Override
	public <T extends Entity> T getUnique(Query<T> query) {
		return getUnique(query, true);
	}

	private <T extends Entity> T findCachedValue(Query<T> query) {
		Collection<T> cachedValues = getValuesFromCache(query.getResultType());
		LOGGER.debug(String.format("cachedValues %s : ", cachedValues));
		if (query instanceof IdQuery) {
			T entity = alias(query.getResultType());
			return from($(entity), cachedValues).where(
					$(entity.getId()).eq(((IdQuery<T>) query).getId()))
					.uniqueResult($(entity));
		}
		if (query instanceof ByViewQuery) {
			T entity = alias(query.getResultType());
			return from($(entity), cachedValues).where(
					buildFullPredicate((ByViewQuery<T>) query)).uniqueResult(
					$(entity));
		}
		return null;
	}

	private Predicate buildFullPredicate(ByViewQuery<?> query) {
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		for (Entry<String, Object> entry : query.getFilter().entrySet()) {
			booleanBuilder.and(createPredicate(query.getResultType(),
					entry.getKey(), entry.getValue()));
		}
		return booleanBuilder.and(query.getPredicate());
	}

	protected Predicate createPredicate(Class<?> type, String key, Object value) {
		checkNotNull(key);
		PathBuilder<?> entityPath = new PathBuilder<>(type,
				CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
						type.getSimpleName()));
		return Expressions.predicate(Ops.EQ, entityPath.get(key),
				value == null ? null : Expressions.constant(value));
	}

	@SuppressWarnings("unchecked")
	private <T extends Entity> Collection<T> getValuesFromCache(Class<T> type) {
		Collection<T> cachedValue = Lists.newArrayList();
		for (HashMultimap<Class<? extends Entity>, Entity> cache : caches) {
			cachedValue.addAll((Collection<? extends T>) cache.get(type));
		}
		return cachedValue;
	}

	@SuppressWarnings("unchecked")
	public <T extends Entity> T getRegisteredValue(T entity) {
		for (HashMultimap<Class<? extends Entity>, Entity> cache : caches) {
			for (Entity result : cache.get(entity.getClass())) {
				if (result.equals(entity))
					return (T) result;
			}
		}
		return null;
	}

	@Override
	public <T extends Entity> T create(Class<T> entityClass) {
		LOGGER.info(String.format("session %s create", getId()));
		T entity = entityBuilder.build(entityClass);
		return sessionize(entity);
	}

	@Override
	public <T extends Entity> T create(Class<T> entityClass, Object... params) {
		T entity = entityBuilder.build(entityClass, params);
		return sessionize(entity);
	}

	protected <T extends Entity> T sessionize(T entity) {
		changeSession(entity);
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
			if (isRegistered(entity, createdObjects)) {
				createdObjects.remove(entity.getClass(), entity);
			}
			if (isRegistered(entity, updatedObjects)) {
				updatedObjects.remove(entity.getClass(), entity);
			}
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
		LOGGER.info(String
				.format("Entity %s record property %s changed oldValue = %s : newValue = %s",
						e.getSource(), e.getPropertyName(), e.getOldValue(),
						e.getNewValue()));
		if (isRegistered(entity, deletedObjects)) {
			commitError(entity, entity.getState(), State.DELETED);
		}
		if (entity.getState() == State.NEW) {
			register(entity, createdObjects);
			return;
		}
		entity.setState(State.MODIFIED);
		register(entity, updatedObjects);
	}

	@Override
	public Session post() {
		LOGGER.info(String.format("post session %s", getId()));
		adapterManager.post(queue);
		return renewSession();
	}

	protected Session renewSession() {
		LOGGER.info(String.format("renew session %s", getId()));
		for (Multimap<Class<? extends Entity>, Entity> cache : caches) {
			cache.clear();
		}
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
		LOGGER.info(String.format("session %s register %s in %s", getId(),
				entity, entity.getState() == State.NEW ? "createdObjects"
						: "updatedObjects"));
		map.put(entity.getClass(), entity);
		LOGGER.info(String.format("map %s size %s", map, map.size()));
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

	private <T extends Entity> void changeSession(Collection<T> entities) {
		for (T entity : entities) {
			if(entity==null)
				continue;
			if (entity.getSession() == null
					|| entity.getState() == State.COMMITED) {
				entity.attributeSession(this);
			}
		}
	}

	public void changeSession(Entity... entities) {
		changeSession(Arrays.asList(entities));
	}

	public UUID getId() {
		return id;
	}

	@Override
	public String toString() {
		return getId().toString();
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
		Session session = Session.class.cast(obj);
		return Objects.equal(this.getId(), session.getId());
	}

}
