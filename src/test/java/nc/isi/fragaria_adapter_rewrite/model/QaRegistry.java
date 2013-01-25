package nc.isi.fragaria_adapter_rewrite.model;

import nc.isi.fragaria_adapter_rewrite.services.FragariaDomainModule;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

public enum QaRegistry {
	INSTANCE;

	private final Registry registry = RegistryBuilder
			.buildAndStartupRegistry(FragariaDomainModule.class);

	public Registry getRegistry() {
		return registry;
	}

}
