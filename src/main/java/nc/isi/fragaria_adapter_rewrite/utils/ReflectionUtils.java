package nc.isi.fragaria_adapter_rewrite.utils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.springframework.beans.BeanUtils;

/**
 * Aide à l'utilisation de Reflection
 * 
 * @author justin
 * 
 */
public final class ReflectionUtils {
	private ReflectionUtils() {

	}

	/**
	 * vérifie si une propriété existe pour une classe donnée
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static boolean propertyExists(Class<?> clazz, String fieldName) {
		return BeanUtils.getPropertyDescriptor(clazz, fieldName) != null;
	}

	/**
	 * récupère la valeur d'une propritété pour un object donné en utilisant la
	 * ReadMethod
	 * 
	 * @param o
	 * @param propertyName
	 * @return
	 */
	public static Object getPropertyValue(Object o, String propertyName) {
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(
				o.getClass(), propertyName);
		try {
			return propertyDescriptor.getReadMethod().invoke(o);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Essaye de récupérer la {@link Class} depuis un {@link Type}
	 * 
	 * @param type
	 * @return
	 */
	public static Class<?> getClass(Type type) {
		try {
			return type.toString().equals("?") ? Object.class : Class
					.forName(type.toString().substring(
							type.toString().lastIndexOf(' ') + 1));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * cherche une annotation sur une propriété ou sa méthode read
	 * <p>
	 * si recursive = true => vérifie sur les méthodes des superclasses
	 * 
	 * @param clazz
	 * @param annotation
	 * @param key
	 * @param recursive
	 * @return
	 */
	public static <T extends Annotation> T getPropertyAnnotation(
			Class<?> clazz, Class<T> annotation, String key, boolean recursive) {
		if (recursive) {
			return getPropertyAnnotation(clazz, annotation, key);
		}
		T fieldAnnotation = null;
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getName().equals(key)) {
				fieldAnnotation = field.getAnnotation(annotation);
				break;
			}
		}
		T methodAnnotation = null;
		try {
			if (propertyExists(clazz, key)) {
				methodAnnotation = clazz
						.getMethod(fieldToGetMethod(clazz, key)).getAnnotation(
								annotation);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			methodAnnotation = null;
		}
		if (fieldAnnotation != null && methodAnnotation != null) {
			throw new RuntimeException(
					"The same annotation is present on the field and its readMethod, please choose one of both");
		}
		return methodAnnotation != null ? methodAnnotation : fieldAnnotation;

	}

	private static String fieldToGetMethod(Class<?> clazz, String key) {
		PropertyDescriptor propertyDescriptor = BeanUtils
				.getPropertyDescriptor(clazz, key);
		Method method = propertyDescriptor.getReadMethod();
		return method != null ? method.getName() : "";
	}

	/**
	 * @see public static <T extends Annotation> T getPropertyAnnotation(
	 *      Class<?> clazz, Class<T> annotation, String key, boolean recursive)
	 *      <p>
	 *      avec recursive = true;
	 * @param clazz
	 * @param annotation
	 * @param key
	 * @return
	 */
	public static <T extends Annotation> T getPropertyAnnotation(
			Class<?> clazz, Class<T> annotation, String key) {
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(clazz,
				key);
		T propertyAnnotation = propertyDescriptor.getReadMethod() != null ? propertyDescriptor
				.getReadMethod().getAnnotation(annotation) : null;
		T fieldAnnotation = null;
		for (Class<?> tempClazz = clazz; Object.class
				.isAssignableFrom(tempClazz) && !tempClazz.equals(Object.class); tempClazz = tempClazz
				.getSuperclass()) {
			for (Field field : tempClazz.getDeclaredFields()) {
				if (field.getName().equals(key)) {
					fieldAnnotation = field.getAnnotation(annotation);
					if (fieldAnnotation != null) {
						break;
					}
				}
			}
		}
		if (fieldAnnotation != null && propertyAnnotation != null) {
			throw new RuntimeException(
					"The same annotation is present on the field and its readMethod, please choose one of both");
		}
		return propertyAnnotation != null ? propertyAnnotation
				: fieldAnnotation;

	}

	/**
	 * récupère la propriété d'une classe par rapport à son nom
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static Field getField(Class<?> clazz, String fieldName)
			throws NoSuchFieldException {
		for (Class<?> tempClazz = clazz; Object.class
				.isAssignableFrom(tempClazz) && !tempClazz.equals(Object.class); tempClazz = tempClazz
				.getSuperclass()) {
			for (Field field : tempClazz.getDeclaredFields()) {
				if (field.getName().equals(fieldName)) {
					return field;
				}
			}
		}
		throw new NoSuchFieldException();

	}

	/**
	 * récupère le {@link PropertyDescriptor} d'une propriété pour une classe
	 * 
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz,
			String key) {
		PropertyDescriptor propertyDescriptor = BeanUtils
				.getPropertyDescriptor(clazz, key);
		if (propertyDescriptor == null) {
			throw new IllegalArgumentException("Pas de propriété " + key + " ("
					+ clazz + ")");
		}
		return propertyDescriptor;
	}

	/**
	 * récupère la valeur d'une annotation pour un type donné
	 * <p>
	 * si recursive = true, cherche dans les super
	 * 
	 * @param clazz
	 * @param annotation
	 * @param recursive
	 * @return
	 */
	public static <T extends Annotation> T getTypeAnnotation(Class<?> clazz,
			Class<T> annotation, boolean recursive) {
		if (recursive) {
			return getTypeAnnotation(clazz, annotation);
		}
		return clazz.getAnnotation(annotation);
	}

	/**
	 * @see <T extends Annotation> T getTypeAnnotation(Class<?> clazz, Class<T>
	 *      annotation, boolean recursive)
	 *      <p>
	 *      avec recursive = true
	 * @param clazz
	 * @param annotation
	 * @return
	 */
	public static <T extends Annotation> T getTypeAnnotation(Class<?> clazz,
			Class<T> annotation) {
		for (Class<?> tempClazz = clazz; Object.class
				.isAssignableFrom(tempClazz) && !Object.class.equals(tempClazz); tempClazz = tempClazz
				.getSuperclass()) {
			if (tempClazz.getAnnotation(annotation) != null) {
				return tempClazz.getAnnotation(annotation);
			}
		}
		return null;

	}
}
