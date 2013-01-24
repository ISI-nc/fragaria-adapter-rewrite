package nc.isi.fragaria_adapter_rewrite.resources;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import nc.isi.fragaria_adapter_rewrite.services.ReflectionFactory;

import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;

public class ResourceFinderImpl implements ResourceFinder {
	private final Reflections reflections;
	private final LoadingCache<String, Set<File>> cache = CacheBuilder
			.newBuilder().maximumSize(10)
			.expireAfterAccess(600L, TimeUnit.SECONDS)
			.build(new CacheLoader<String, Set<File>>() {

				@Override
				public Set<File> load(String key) throws Exception {
					Set<File> resources = Sets.newHashSet();
					Set<String> resFiles = reflections.getResources(Pattern
							.compile(key));
					for (String res : resFiles) {
						resources.add(FileUtils.toFile(this.getClass()
								.getResource("/" + res)));
					}
					return resources;
				}
			});

	public ResourceFinderImpl(ReflectionFactory reflectionFactory,
			Collection<String> packageNames) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		for (String packageName : packageNames) {
			configurationBuilder.addUrls(ClasspathHelper
					.forPackage(packageName));
		}
		reflections = reflectionFactory.create(configurationBuilder
				.setScanners(new ResourcesScanner()));
	}

	@Override
	public Set<File> getResourcesMatching(String regExp) {
		try {
			return cache.get(regExp);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
