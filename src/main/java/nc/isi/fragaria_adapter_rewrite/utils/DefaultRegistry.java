package nc.isi.fragaria_adapter_rewrite.utils;

import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

public class DefaultRegistry {

	private static Registry registry = null;

	public static <T> T getService(Class<T> clazz) {
		return getInstance().getService(clazz);
	}

	public static Registry getInstance() {
		if (registry == null) {
			initRegistry();
		}
		return registry;
	}

	public static void setInstance(Registry registry) {
		DefaultRegistry.registry = registry;
	}

	private static synchronized void initRegistry() {
		if (registry != null) {
			return;
		}

		RegistryBuilder builder = new RegistryBuilder();

		IOCUtilities.addDefaultModules(builder);

		registry = builder.build();
		registry.performRegistryStartup();
	}

}
