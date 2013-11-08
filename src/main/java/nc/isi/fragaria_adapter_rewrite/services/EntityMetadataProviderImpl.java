package nc.isi.fragaria_adapter_rewrite.services;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.ExecutionException;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_reflection.services.ObjectMetadataProviderImpl;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class EntityMetadataProviderImpl extends ObjectMetadataProviderImpl
		implements EntityMetadataProvider {

	private final LoadingCache<Class<? extends Entity>, EntityMetadata> cache = CacheBuilder
			.newBuilder().build(
					new CacheLoader<Class<? extends Entity>, EntityMetadata>() {

						@Override
						public EntityMetadata load(Class<? extends Entity> key) {
							return new EntityMetadata(key);
						}

					});

	@SuppressWarnings("unchecked")
	@Override
	public EntityMetadata provide(Class<?> typeClass) {
		checkArgument(Entity.class.isAssignableFrom(typeClass),
				"seuls les %s peuvent produire des %s", Entity.class,
				EntityMetadata.class);
		try {
			return cache.get((Class<? extends Entity>) typeClass);
		} catch (ExecutionException e) {
			throw Throwables.propagate(e);
		}
	}

}
