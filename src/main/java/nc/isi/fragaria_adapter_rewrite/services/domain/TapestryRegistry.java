package nc.isi.fragaria_adapter_rewrite.services.domain;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

public enum TapestryRegistry {
	INSTANCE;

	private final Registry registry = RegistryBuilder
			.buildAndStartupRegistry(FragariaDomainModule.class);

	public Registry getRegistry() {
		return registry;
	}
}
