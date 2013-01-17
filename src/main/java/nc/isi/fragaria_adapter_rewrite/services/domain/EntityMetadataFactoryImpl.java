package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Map;

import com.google.common.collect.Maps;

public class EntityMetadataFactoryImpl implements EntityMetadataFactory {
	private final Map<Class<? extends Entity>, EntityMetadata> cache = Maps
			.newHashMap();

	@Override
	public EntityMetadata create(Class<? extends Entity> entityClass) {
		if (!cache.containsKey(entityClass)) {
			cache.put(entityClass, new EntityMetadata(entityClass));
		}
		return cache.get(entityClass);
	}

}
