package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.concurrent.ExecutionException;

import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ReflectionFactory {
	private final LoadingCache<Configuration, Reflections> cache = CacheBuilder
			.newBuilder().build(new CacheLoader<Configuration, Reflections>() {

				@Override
				public Reflections load(Configuration key) throws Exception {
					return new Reflections(key);
				}

			});

	public Reflections create(String packageName) {
		return create(ConfigurationBuilder.build(packageName));
	}

	public Reflections create(Configuration configuration) {
		try {
			return cache.get(configuration);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
