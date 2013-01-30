package nc.isi.fragaria_adapter_rewrite.entities;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import nc.isi.fragaria_adapter_rewrite.annotations.BackReference;
import nc.isi.fragaria_adapter_rewrite.annotations.DsKey;
import nc.isi.fragaria_adapter_rewrite.annotations.Embeded;
import nc.isi.fragaria_adapter_rewrite.annotations.InView;
import nc.isi.fragaria_adapter_rewrite.annotations.Partial;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericEmbedingViews.Id;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericQueryViews.All;
import nc.isi.fragaria_adapter_rewrite.entities.views.View;
import nc.isi.fragaria_reflection.utils.ReflectionUtils;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class EntityMetadata {
	private final Class<? extends Entity> entityClass;
	private ImmutableSet<String> propertyNames;
	private LoadingCache<String, PropertyDescriptor> cache = CacheBuilder
			.newBuilder().build(new CacheLoader<String, PropertyDescriptor>() {

				@Override
				public PropertyDescriptor load(String key) {
					return checkNotNull(BeanUtils.getPropertyDescriptor(
							entityClass, key));
				}
			});

	private Multimap<Class<? extends View>, String> viewProperties = HashMultimap
			.create();
	private boolean viewPropertiesInitialized = false;
	private String dsKey;

	public EntityMetadata(Class<? extends Entity> entityClass) {
		this.entityClass = entityClass;
	}

	public String getDsKey() {
		if (dsKey == null) {
			DsKey annotation = ReflectionUtils.getTypeAnnotation(entityClass,
					DsKey.class);
			dsKey = checkNotNull(annotation).value();
		}
		return dsKey;
	}

	public Class<? extends View> getEmbeded(String propertyName) {
		Embeded embeded = getPropertyAnnotation(propertyName, Embeded.class);
		return embeded != null ? embeded.value() : Entity.class
				.isAssignableFrom(propertyType(propertyName)) ? Id.class : null;
	}

	public Class<? extends View> getPartial(String propertyName) {
		Partial partial = getPropertyAnnotation(propertyName, Partial.class);
		return partial != null ? partial.value() : All.class;
	}

	public String getBackReference(String propertyName) {
		BackReference reference = getPropertyAnnotation(propertyName,
				BackReference.class);
		return reference != null ? (reference.value().equals(
				BackReference.DEFAULT) ? entityClass.getSimpleName()
				.substring(0, 1).toLowerCase()
				+ entityClass.getSimpleName().substring(1) : reference.value())
				: null;
	}

	protected <T extends Annotation> T getPropertyAnnotation(
			String propertyName, Class<T> annotation) {
		return getPropertyDescriptor(propertyName).getReadMethod()
				.getAnnotation(annotation);
	}

	public ImmutableSet<String> propertyNames() {
		if (propertyNames == null) {
			Set<String> temp = Sets.newHashSet();
			for (PropertyDescriptor propertyDescriptor : BeanUtils
					.getPropertyDescriptors(entityClass)) {
				String propertyName = propertyDescriptor.getName();
				temp.add(propertyName);
			}
			propertyNames = ImmutableSet.copyOf(temp);
		}
		return propertyNames;
	}

	@SuppressWarnings("unchecked")
	public Collection<String> propertyNames(Class<? extends View> view) {
		initViewProperties();
		Collection<String> properties = viewProperties.get(view);
		if (View.class.isAssignableFrom(view.getSuperclass())) {
			properties.addAll(propertyNames((Class<? extends View>) view
					.getSuperclass()));
		}
		return properties;
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

	public Class<?> propertyType(String propertyName) {
		return getPropertyDescriptor(propertyName).getPropertyType();
	}

	public String getJsonPropertyName(String propertyName) {
		JsonProperty jsonProperty = getPropertyAnnotation(propertyName,
				JsonProperty.class);
		return jsonProperty == null ? propertyName : jsonProperty.value();
	}

	@SuppressWarnings("unchecked")
	public <T extends View> Collection<Class<? extends T>> getViews(
			Class<T> viewType) {
		checkNotNull(viewType);
		initViewProperties();
		Collection<Class<? extends T>> views = Sets.newHashSet();
		for (Class<? extends View> view : viewProperties.keySet()) {
			if (viewType.isAssignableFrom(view)) {
				views.add((Class<? extends T>) view);
			}
		}
		return views;
	}

	/**
	 * 
	 * Renvoie les types des paramètres de la propriété
	 * 
	 * @param propertyName
	 * @return
	 * @return
	 */
	public Class<?>[] propertyParameterClasses(String propertyName) {
		Type type = getPropertyDescriptor(propertyName).getReadMethod()
				.getGenericReturnType();
		if (type instanceof ParameterizedType) {
			ParameterizedType realType = ParameterizedType.class.cast(type);
			int length = realType.getActualTypeArguments().length;
			Class<?>[] classes = new Class[length];
			for (int i = 0; i < length; i++) {
				classes[i] = ReflectionUtils.getClass(realType
						.getActualTypeArguments()[i]);
			}
			return classes;
		}
		return new Class[0];
	}

	public PropertyDescriptor getPropertyDescriptor(String propertyName) {
		try {
			return cache.get(propertyName);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public Object read(Entity entity, String propertyName) {
		try {
			return getPropertyDescriptor(propertyName).getReadMethod().invoke(
					entity, (Object[]) null);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}

	}

	public Boolean canWrite(String propertyName) {
		return getPropertyDescriptor(propertyName).getWriteMethod() != null;
	}

	public void write(Entity entity, String propertyName, Object value) {
		try {
			checkNotNull(getPropertyDescriptor(propertyName).getWriteMethod())
					.invoke(entity, value);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
