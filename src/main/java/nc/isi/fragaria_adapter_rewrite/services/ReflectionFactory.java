package nc.isi.fragaria_adapter_rewrite.services;

import java.util.Collection;

import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;

/**
 * Une factory pour générer des {@link Reflections} associé à un nom de package
 * ou à une {@link Configuration}
 * 
 * @author justin
 * 
 */
public interface ReflectionFactory {

	Reflections create(String packageName);

	Reflections create(Collection<String> packageNames);

	Reflections create(Configuration configuration);

	Reflections create(Collection<String> packageNames, Scanner... scanners);

}