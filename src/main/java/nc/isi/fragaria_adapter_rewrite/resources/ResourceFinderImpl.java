package nc.isi.fragaria_adapter_rewrite.resources;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import nc.isi.fragaria_adapter_rewrite.services.ReflectionFactory;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;

public class ResourceFinderImpl implements ResourceFinder {
	private static final long MAX_FILES_TIME = 10L;
	private static final Logger LOGGER = Logger
			.getLogger(ResourceFinderImpl.class);
	private final Reflections reflections;
	private final LoadingCache<String, Set<File>> cache = CacheBuilder
			.newBuilder().expireAfterAccess(MAX_FILES_TIME, TimeUnit.MINUTES)
			.build(new CacheLoader<String, Set<File>>() {

				@Override
				public Set<File> load(String key) {
					Set<File> resources = Sets.newHashSet();
					Set<String> resFiles = reflections.getResources(Pattern
							.compile(key));
					for (String res : resFiles) {
						LOGGER.info(res);
						resources.add(FileUtils.toFile(this.getClass()
								.getResource("/" + res)));
					}
					return resources;
				}
			});

	public ResourceFinderImpl(ReflectionFactory reflectionFactory,
			Collection<String> packageNames) {
		reflections = reflectionFactory.create(packageNames,
				new ResourcesScanner());
	}

	@Override
	public Set<File> getResourcesMatching(String regExp) {
		LOGGER.info(String.format("looking for : " + regExp));
		try {
			return cache.get(regExp);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
