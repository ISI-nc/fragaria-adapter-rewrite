package nc.isi.fragaria_adapter_rewrite.resources.mock;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.services.ReflectionFactory;

import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.util.ClasspathHelper;
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

	@Override
	public Reflections create(Collection<String> packageNames) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		for (String packageName : packageNames) {
			configurationBuilder.addUrls(ClasspathHelper
					.forPackage(packageName));
		}
		return create(configurationBuilder);
	}

	@Override
	public Reflections create(Collection<String> packageNames,
			Scanner... scanners) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		for (String packageName : packageNames) {
			configurationBuilder.addUrls(ClasspathHelper
					.forPackage(packageName));
		}
		configurationBuilder.setScanners(scanners);
		return create(configurationBuilder);
	}

}
