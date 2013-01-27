package nc.isi.fragaria_adapter_rewrite.services;

import org.reflections.Configuration;
import org.reflections.Reflections;

public interface ReflectionFactory {

	public Reflections create(String packageName);

	public Reflections create(Configuration configuration);

}