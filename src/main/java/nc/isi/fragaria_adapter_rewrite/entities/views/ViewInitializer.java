package nc.isi.fragaria_adapter_rewrite.entities.views;

import java.lang.reflect.Modifier;
import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.services.ReflectionFactory;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class ViewInitializer {
	private static final Logger LOGGER = Logger
			.getLogger(ViewInitializer.class);
	private final ViewGeneratorManager viewGenerator;
	private final Reflections reflections;

	public ViewInitializer(ViewGeneratorManager viewGenerator,
			ReflectionFactory reflectionFactory, Collection<String> packageNames) {
		LOGGER.info("packageNames : " + packageNames);
		this.viewGenerator = viewGenerator;
		this.reflections = reflectionFactory.create(packageNames,
				new SubTypesScanner());
	}

	public void initialize() {
		for (Class<? extends Entity> entityClass : reflections
				.getSubTypesOf(Entity.class)) {
			if (Modifier.isAbstract(entityClass.getModifiers())
					|| entityClass.isAnonymousClass()
					|| entityClass.isInterface())
				continue;
			LOGGER.info("initialize : " + entityClass);
			viewGenerator.generate(entityClass);
		}
	}
}
