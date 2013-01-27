package nc.isi.fragaria_adapter_rewrite.services;

import org.reflections.Configuration;
import org.reflections.Reflections;

/**
 * Une factory pour générer des {@link Reflections} associé à un nom de package
 * ou à une {@link Configuration}
 * 
 * @author justin
 * 
 */
public interface ReflectionFactory {

	public Reflections create(String packageName);

	public Reflections create(Configuration configuration);

}