package nc.isi.fragaria_adapter_rewrite.services;

import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ReflectionFactoryImpl implements ReflectionFactory {
	private static final Logger LOGGER = Logger
			.getLogger(ReflectionFactoryImpl.class);
	private final LoadingCache<Configuration, Reflections> cache = CacheBuilder
			.newBuilder().build(new CacheLoader<Configuration, Reflections>() {

				@Override
				public Reflections load(Configuration key) {
					LOGGER.info("building reflection for configuration : "
							+ key);
					return new Reflections(key);
				}

			});

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nc.isi.fragaria_adapter_rewrite.services.ReflectionFactory#create(java
	 * .lang.String)
	 */
	@Override
	public Reflections create(String packageName) {
		return create(ConfigurationBuilder.build(packageName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nc.isi.fragaria_adapter_rewrite.services.ReflectionFactory#create(org
	 * .reflections.Configuration)
	 */
	@Override
	public Reflections create(Configuration configuration) {
		try {
			return cache.get(configuration);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
