package nc.isi.fragaria_adapter_rewrite.entities;

import static junit.framework.Assert.assertEquals;
import nc.isi.fragaria_adapter_rewrite.model.QaRegistry;

import org.joda.time.DateTime;
import org.junit.Test;

public class TestEntityBuilder {

	@Test
	public void testEntityBuilder() {
		DateTime dateTime = new DateTime();
		EntityBuilder entityBuilder = QaRegistry.INSTANCE.getRegistry()
				.getService(EntityBuilder.class);
		ComplexObject complexObject = entityBuilder.build(ComplexObject.class,
				"test", dateTime, "a", "b");
		assertEquals("test", complexObject.getTest());
		assertEquals(dateTime, complexObject.getCreation());
		assertEquals("a", complexObject.getA());
		assertEquals("b", complexObject.getB());
	}

}
