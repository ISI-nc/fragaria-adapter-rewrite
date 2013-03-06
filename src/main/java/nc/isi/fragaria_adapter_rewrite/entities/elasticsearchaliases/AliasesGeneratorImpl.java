package nc.isi.fragaria_adapter_rewrite.entities.elasticsearchaliases;

import nc.isi.fragaria_adapter_rewrite.dao.adapters.ElasticSearchAdapter;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;

import org.apache.log4j.Logger;

public class AliasesGeneratorImpl implements AliasesGenerator {
	private static final Logger LOGGER = Logger
			.getLogger(AliasesGeneratorImpl.class);
	private final ElasticSearchAdapter elasticSearchAdapter;

	public AliasesGeneratorImpl(ElasticSearchAdapter elasticSearchAdapter) {
		super();
		this.elasticSearchAdapter = elasticSearchAdapter;
	}

	@Override
	public void generate(EntityMetadata entityMetadata) {
		if (entityMetadata.getEsAlias() == null) {
			LOGGER.info("no alias found");
		} else {
			LOGGER.info("build alias : " + entityMetadata.getEsAlias());
			build(entityMetadata);
		}
	}

	private void build(EntityMetadata entityMetadata) {
		if (exists(entityMetadata.getEsAlias())) {
			LOGGER.info("alias already exists");
			return;
		}
		elasticSearchAdapter.build(entityMetadata);
		LOGGER.info("alias created");
	}

	private boolean exists(String alias) {
		return elasticSearchAdapter.exists(alias);
	}

}
