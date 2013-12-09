package nc.isi.fragaria_adapter_rewrite.entities;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import nc.isi.fragaria_adapter_rewrite.annotations.BackReference;
import nc.isi.fragaria_adapter_rewrite.annotations.CollectionType;
import nc.isi.fragaria_adapter_rewrite.annotations.DsKey;
import nc.isi.fragaria_adapter_rewrite.annotations.Embeded;
import nc.isi.fragaria_adapter_rewrite.annotations.EsAlias;
import nc.isi.fragaria_adapter_rewrite.annotations.InView;
import nc.isi.fragaria_adapter_rewrite.annotations.Partial;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericEmbedingViews.Full;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericQueryViews.All;
import nc.isi.fragaria_adapter_rewrite.entities.views.View;
import nc.isi.fragaria_reflection.utils.DefaultObjectMetadata;
import nc.isi.fragaria_reflection.utils.ReflectionUtils;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class EntityMetadata extends DefaultObjectMetadata {
	private static final Logger LOGGER = Logger.getLogger(EntityMetadata.class);
	private static final Collection<String> excludedProperties = Arrays
			.asList("class");
	private static final String ID_TOKEN = "._id";
	private static final String ALIAS_SUFFIX = "_alias";
	private final Class<? extends Entity> entityClass;
	private final String esAlias;

	private Multimap<Class<? extends View>, String> viewProperties = HashMultimap
			.create();
	private boolean viewPropertiesInitialized = false;
	private String dsKey;
	private ImmutableSet<String> writableProperyNamesCache;
	private final LoadingCache<String, Boolean> isNotEmbededListCache = CacheBuilder
			.newBuilder().build(new CacheLoader<String, Boolean>() {

				@Override
				public Boolean load(String key) throws Exception {
					Class<?> propertyType = propertyType(key);
					return Collection.class.isAssignableFrom(propertyType)
							&& Entity.class
									.isAssignableFrom(propertyParameterClasses(key)[0])
							&& getEmbeded(key) == null;
				}

			});

	private final LoadingCache<String, Boolean> isNaturallyEmbededCache = CacheBuilder
			.newBuilder().build(new CacheLoader<String, Boolean>() {

				@Override
				public Boolean load(String key) throws Exception {
					Class<?> propertyType = propertyType(key);
					return Collection.class.isAssignableFrom(propertyType)
							&& !Entity.class
									.isAssignableFrom(propertyParameterClasses(key)[0]);
				}

			});

	private final Map<String, Class<? extends Collection>> collectionTypeCache = Maps
			.newHashMap();

	private final Map<String, Class<? extends View>> embededCache = Maps
			.newHashMap();

	private final LoadingCache<String, Class<? extends View>> partialCache = CacheBuilder
			.newBuilder().build(
					new CacheLoader<String, Class<? extends View>>() {

						@Override
						public Class<? extends View> load(String key)
								throws Exception {
							Partial partial = getPropertyAnnotation(key,
									Partial.class);
							return partial != null ? partial.value()
									: All.class;
						}

					});

	private final LoadingCache<String, String> backReferenceCache = CacheBuilder
			.newBuilder().build(new CacheLoader<String, String>() {

				@Override
				public String load(String key) throws Exception {
					BackReference reference = getPropertyAnnotation(key,
							BackReference.class);
					LOGGER.debug(String.format("backReference : %s",
							reference != null ? reference.value() : entityClass
									.getSimpleName().substring(0, 1)
									.toLowerCase()
									+ entityClass.getSimpleName().substring(1)));
					return reference != null ? reference.value() : entityClass
							.getSimpleName().substring(0, 1).toLowerCase()
							+ entityClass.getSimpleName().substring(1);
				}

			});

	private LoadingCache<Class<? extends View>, Collection<String>> propertyNamesByViewCache = CacheBuilder
			.newBuilder()
			.build(new CacheLoader<Class<? extends View>, Collection<String>>() {

				@SuppressWarnings("unchecked")
				@Override
				public Collection<String> load(Class<? extends View> key)
						throws Exception {
					initViewProperties();
					if (Full.class.isAssignableFrom(key)) {
						return propertyNames();
					}
					Collection<String> properties = viewProperties.get(key);
					if (View.class.isAssignableFrom(key.getSuperclass())) {
						properties
								.addAll(propertyNames((Class<? extends View>) key
										.getSuperclass()));
					}
					return properties;
				}

			});

	private LoadingCache<String, String> jsonPropertyNameCache = CacheBuilder
			.newBuilder().build(new CacheLoader<String, String>() {

				@Override
				public String load(String key) throws Exception {
					JsonProperty jsonProperty = getPropertyAnnotation(key,
							JsonProperty.class);
					return jsonProperty == null ? key : jsonProperty.value();
				}

			});

	private LoadingCache<Class<? extends View>, Collection<Class<? extends View>>> viewsCache = CacheBuilder
			.newBuilder()
			.build(new CacheLoader<Class<? extends View>, Collection<Class<? extends View>>>() {

				@Override
				public Collection<Class<? extends View>> load(
						Class<? extends View> key) throws Exception {
					checkNotNull(key);
					Collection<Class<? extends View>> views = Sets.newHashSet();
					for (Class<? extends View> view : viewProperties.keySet()) {
						if (key.isAssignableFrom(view)) {
							views.add(view);
						}
					}
					return views;
				}

			});

	public EntityMetadata(Class<? extends Entity> entityClass) {
		super(entityClass);
		this.entityClass = entityClass;
		this.esAlias = initEsAlias();
		initViewProperties();
	}

	/**
	 * 
	 * @return
	 */
	public ImmutableSet<String> writablesPropertyNames() {
		if (writableProperyNamesCache == null) {
			Set<String> writableProperties = Sets.newHashSet();
			for (String name : propertyNames()) {
				if (getPropertyAnnotation(name, JsonIgnore.class) != null) {
					continue;
				}
				if (excludedProperties.contains(name)) {
					continue;
				}
				writableProperties.add(name);
			}
			LOGGER.info(writableProperties);
			writableProperyNamesCache = ImmutableSet.copyOf(writableProperties);
		}
		return writableProperyNamesCache;
	}

	public boolean isNotEmbededList(String propertyName) {
		try {
			return isNotEmbededListCache.get(propertyName);
		} catch (ExecutionException e) {
			throw Throwables.propagate(e);
		}
	}

	public boolean isNaturalyEmbeded(String propertyName) {
		try {
			return isNaturallyEmbededCache.get(propertyName);
		} catch (ExecutionException e) {
			throw Throwables.propagate(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public Class<? extends Collection> getCollectionType(String propertyName) {
		if (!collectionTypeCache.keySet().contains(propertyName)) {
			CollectionType collectionType = getPropertyAnnotation(propertyName,
					CollectionType.class);
			collectionTypeCache.put(propertyName,
					collectionType != null ? collectionType.value() : null);
		}
		return collectionTypeCache.get(propertyName);
	}

	public String getDsKey() {
		if (dsKey == null) {
			DsKey annotation = ReflectionUtils.getTypeAnnotation(entityClass,
					DsKey.class);
			dsKey = checkNotNull(annotation).value();
		}
		return dsKey;
	}

	public String getEsAlias() {
		return esAlias;
	}

	protected String initEsAlias() {
		EsAlias annotation = ReflectionUtils.getTypeAnnotation(entityClass,
				EsAlias.class);
		if (annotation == null)
			return null;
		return annotation.value().equals("") ? entityClass.getSimpleName()
				+ ALIAS_SUFFIX : annotation.value();
	}

	public Class<? extends View> getEmbeded(String propertyName) {
		if (!embededCache.keySet().contains(propertyName)) {
			Embeded embeded = getPropertyAnnotation(propertyName, Embeded.class);
			embededCache.put(propertyName, embeded != null ? embeded.value()
					: null);
		}
		return embededCache.get(propertyName);
	}

	public Class<? extends View> getPartial(String propertyName) {
		try {
			return partialCache.get(propertyName);
		} catch (ExecutionException e) {
			throw Throwables.propagate(e);
		}
	}

	public String getBackReference(String propertyName) {
		try {
			return backReferenceCache.get(propertyName);
		} catch (ExecutionException e) {
			throw Throwables.propagate(e);
		}
	}

	public Collection<String> propertyNames(Class<? extends View> view) {
		try {
			return propertyNamesByViewCache.get(view);
		} catch (ExecutionException e) {
			throw Throwables.propagate(e);
		}
	}

	protected void initViewProperties() {
		if (!viewPropertiesInitialized) {
			for (String name : propertyNames()) {
				InView annotation = getPropertyAnnotation(name, InView.class);
				if (annotation != null) {
					for (Class<? extends View> tempView : annotation.value()) {
						viewProperties.put(tempView, name);
					}
				}
			}
			viewPropertiesInitialized = true;
		}
	}

	public Class<? extends Entity> getEntityClass() {
		return entityClass;
	}

	public String getJsonPropertyName(String propertyName) {
		try {
			return jsonPropertyNameCache.get(propertyName);
		} catch (ExecutionException e) {
			throw Throwables.propagate(e);
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<Class<? extends View>> getViews(
			Class<? extends View> viewType) {
		try {
			return viewsCache.get(viewType);
		} catch (ExecutionException e) {
			throw Throwables.propagate(e);
		}
	}

}
