package nc.isi.fragaria_adapter_rewrite.entities.elasticsearchaliases;

import java.lang.reflect.Modifier;

import nc.isi.fragaria_adapter_rewrite.entities.AbstractEntity;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewInitializer;
import nc.isi.fragaria_reflection.services.ReflectionProvider;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
/**
 * 
 * @author bjonathas
 *Initialize filtered aliases for entities annotated with 
 *@Elasticsearch(aliasname)
 */
public class AliasesInitializer {
	private static final Logger LOGGER = Logger
			.getLogger(ViewInitializer.class);
	private final Reflections reflections;
	private AliasesGenerator generator;

	public AliasesInitializer(AliasesGenerator generator,
			ReflectionProvider reflectionProvider) {
		this.reflections = reflectionProvider.provide();
		this.generator = generator;
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
			initializeIfEsAlias(new EntityMetadata(entityClass));	
		}
	}

	private void initializeIfEsAlias(EntityMetadata entityMetadata) {
		if (entityMetadata.getEsAlias() == null) {
			LOGGER.info("no alias found for "+entityMetadata.getEntityClass());
		} else {
			LOGGER.info("initialize : " + entityMetadata.getEntityClass());
			generator.generate(entityMetadata);
		}
		
	}
}
