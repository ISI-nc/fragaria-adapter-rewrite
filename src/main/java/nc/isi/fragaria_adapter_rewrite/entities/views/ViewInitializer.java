package nc.isi.fragaria_adapter_rewrite.entities.views;

import nc.isi.fragaria_adapter_rewrite.entities.AbstractEntity;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.services.EntityMetadataProvider;
import nc.isi.fragaria_reflection.services.ReflectionProvider;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

public class ViewInitializer {
	private static final Logger LOGGER = Logger
			.getLogger(ViewInitializer.class);
	private final ViewGeneratorManager viewGenerator;
	private final Reflections reflections;
	private final EntityMetadataProvider entityMetadataProvider;

	public ViewInitializer(ViewGeneratorManager viewGenerator,
			ReflectionProvider reflectionProvider,
			EntityMetadataProvider entityMetadataProvider) {
		this.viewGenerator = viewGenerator;
		this.reflections = reflectionProvider.provide();
		this.entityMetadataProvider = entityMetadataProvider;
	}

	public void initialize() {
		LOGGER.info("begin view initialization");
		for (Class<? extends Entity> entityClass : reflections
				.getSubTypesOf(AbstractEntity.class)) {
			if (!hasDsKey(entityClass) || entityClass.isAnonymousClass()
					|| entityClass.isInterface()) {
				LOGGER.info("rejected : " + entityClass);
				continue;
			}
			LOGGER.info("initialize : " + entityClass);
			viewGenerator.generate(entityClass);
		}
	}

	private boolean hasDsKey(Class<? extends Entity> entityClass) {
		EntityMetadata entityMetadata = entityMetadataProvider
				.provide(entityClass);
		try {
			entityMetadata.getDsKey();
			return true;
		} catch (NullPointerException n) {
			return false;
		}
	}
}
