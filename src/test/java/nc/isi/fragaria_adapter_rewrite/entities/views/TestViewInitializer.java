package nc.isi.fragaria_adapter_rewrite.entities.views;

import junit.framework.TestCase;

import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.Test;

public class TestViewInitializer extends TestCase {

	@Test
	public void testInitialize() {
		RegistryBuilder
				.buildAndStartupRegistry(ViewInitializerTestModule.class);
	}

}
