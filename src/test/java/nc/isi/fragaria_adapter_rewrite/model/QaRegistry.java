package nc.isi.fragaria_adapter_rewrite.model;

import nc.isi.fragaria_adapter_rewrite.entities.views.ViewInitializerTestModule;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

public enum QaRegistry {
	INSTANCE;

	private final Registry registry = RegistryBuilder
			.buildAndStartupRegistry(ViewInitializerTestModule.class);

	public Registry getRegistry() {
		return registry;
	}

}
