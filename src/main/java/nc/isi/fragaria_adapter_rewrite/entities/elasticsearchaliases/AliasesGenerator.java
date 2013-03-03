package nc.isi.fragaria_adapter_rewrite.entities.elasticsearchaliases;

import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;

/**
 * 
 * @author bjonathas
 *
 *generate filtered alias for entities annotated with @EsAlias
 */
public interface AliasesGenerator {
	void generate(EntityMetadata entityMetadata);
}
