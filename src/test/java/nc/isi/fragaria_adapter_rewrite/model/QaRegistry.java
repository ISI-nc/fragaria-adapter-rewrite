package nc.isi.fragaria_adapter_rewrite.model;

import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;

public enum QaRegistry {
	INSTANCE;

	private final Registry registry = IOCUtilities.buildDefaultRegistry();

	public Registry getRegistry() {
		return registry;
	}

}
