package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.reflections.Reflections;
import org.springframework.beans.BeanUtils;

public class ReflectionUtils {
	public final static Reflections REFLECTIONS = new Reflections(
			"nc.isi.fragaria_adapter_rewrite");

	public static boolean propertyExists(Class<?> clazz, String fieldName) {
		return BeanUtils.getPropertyDescriptor(clazz, fieldName) != null;
	}

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

	public static Class<?> getClass(Type type) {
		try {
			if (type.toString() == "?")
				return Object.class;
			return Class.forName(type.toString().substring(
					type.toString().lastIndexOf(' ') + 1));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends Annotation> T getPropertyAnnotation(
			Class<?> clazz, Class<T> annotation, String key, boolean recursive) {
		if (recursive)
			return getPropertyAnnotation(clazz, annotation, key);
		T fieldAnnotation = null;
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getName().equals(key)) {
				fieldAnnotation = field.getAnnotation(annotation);
				break;
			}
		}
		T methodAnnotation = null;
		try {
			if (propertyExists(clazz, key))
				methodAnnotation = clazz
						.getMethod(fieldToGetMethod(clazz, key)).getAnnotation(
								annotation);
		} catch (NoSuchMethodException | SecurityException e) {
			methodAnnotation = null;
		}
		if (fieldAnnotation != null && methodAnnotation != null)
			throw new RuntimeException(
					"The same annotation is present on the field and its readMethod, please choose one of both");
		return methodAnnotation != null ? methodAnnotation : fieldAnnotation;

	}

	private static String fieldToGetMethod(Class<?> clazz, String key) {
		PropertyDescriptor propertyDescriptor = BeanUtils
				.getPropertyDescriptor(clazz, key);
		Method method = propertyDescriptor.getReadMethod();
		return method != null ? method.getName() : "";
	}

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
					if (fieldAnnotation != null)
						break;
				}
			}
		}
		if (fieldAnnotation != null && propertyAnnotation != null)
			throw new RuntimeException(
					"The same annotation is present on the field and its readMethod, please choose one of both");
		return propertyAnnotation != null ? propertyAnnotation
				: fieldAnnotation;

	}

	public static Field getField(Class<?> clazz, String fieldName)
			throws NoSuchFieldException {
		for (Class<?> tempClazz = clazz; Object.class
				.isAssignableFrom(tempClazz) && !tempClazz.equals(Object.class); tempClazz = tempClazz
				.getSuperclass()) {
			for (Field field : tempClazz.getDeclaredFields()) {
				if (field.getName().equals(fieldName))
					return field;
			}
		}
		throw new NoSuchFieldException();

	}

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

	public static <T extends Annotation> T getTypeAnnotation(Class<?> clazz,
			Class<T> annotation, boolean recursive) {
		if (recursive)
			return getTypeAnnotation(clazz, annotation);
		return clazz.getAnnotation(annotation);
	}

	public static <T extends Annotation> T getTypeAnnotation(Class<?> clazz,
			Class<T> annotation) {
		for (Class<?> tempClazz = clazz; Object.class
				.isAssignableFrom(tempClazz) && !Object.class.equals(tempClazz); tempClazz = tempClazz
				.getSuperclass()) {
			if (tempClazz.getAnnotation(annotation) != null)
				return tempClazz.getAnnotation(annotation);
		}
		return null;

	}

	public static Class<?>[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	private static List<Class<?>> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));

			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}
}
