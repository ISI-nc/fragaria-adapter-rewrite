package nc.isi.fragaria_adapter_rewrite.entities.views;

import java.lang.reflect.Modifier;

import nc.isi.fragaria_adapter_rewrite.entities.AbstractEntity;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_reflection.services.ReflectionProvider;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

public class ViewInitializer {
	private static final Logger LOGGER = Logger
			.getLogger(ViewInitializer.class);
	private final ViewGeneratorManager viewGenerator;
	private final Reflections reflections;

	public ViewInitializer(ViewGeneratorManager viewGenerator,
			ReflectionProvider reflectionProvider) {
		this.viewGenerator = viewGenerator;
		this.reflections = reflectionProvider.provide();
	}

	public void initialize() {
		LOGGER.info("begin view initialization");
		for (Class<? extends Entity> entityClass : reflections
				.getSubTypesOf(AbstractEntity.class)) {
			if (Modifier.isAbstract(entityClass.getModifiers())
					|| entityClass.isAnonymousClass()
					|| entityClass.isInterface()) {
				LOGGER.info("rejected : " + entityClass);
				continue;
			}
			LOGGER.info("initialize : " + entityClass);
			viewGenerator.generate(entityClass);
		}
	}
}
