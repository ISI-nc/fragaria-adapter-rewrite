package nc.isi.fragaria_adapter_rewrite.services;

import junit.framework.TestCase;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.model.QaRegistry;
import nc.isi.fragaria_adapter_rewrite.utils.jackson.EntityJacksonModule;

import org.joda.time.DateTime;
import org.junit.Test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperProviderImplTest extends TestCase {

	private EntityJacksonModule module = QaRegistry.INSTANCE.getRegistry()
			.autobuild(EntityJacksonModule.class);
	private static final MapperFeature[] mapperFeatures = { MapperFeature.DEFAULT_VIEW_INCLUSION };

	@Test
	public void testProvide() {
		ObjectMapperProvider objectMapperProvider = new ObjectMapperProviderImpl(
				module);
		ObjectMapper objectMapper = objectMapperProvider.provide();
		assertTrue("l'objectMapper ne peut pas serialize Entity",
				objectMapper.canSerialize(Entity.class));
		assertTrue("l'objectMapper ne peut pas serialize joda.DateTime",
				objectMapper.canSerialize(DateTime.class));
		for (MapperFeature feature : mapperFeatures) {
			assertTrue(String.format("%s enabled", feature),
					!objectMapper.isEnabled(feature));
		}
		ObjectMapper second = objectMapperProvider.provide();
		assertSame("pas la même référence", objectMapper, second);
	}

}
