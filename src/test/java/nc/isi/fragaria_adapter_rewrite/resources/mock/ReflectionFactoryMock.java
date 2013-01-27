package nc.isi.fragaria_adapter_rewrite.resources.mock;

import nc.isi.fragaria_adapter_rewrite.services.ReflectionFactory;

import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

public class ReflectionFactoryMock implements ReflectionFactory {

	@Override
	public Reflections create(String packageName) {
		return create(ConfigurationBuilder.build(packageName));
	}

	@Override
	public Reflections create(Configuration configuration) {
		return new Reflections(configuration);
	}

}
