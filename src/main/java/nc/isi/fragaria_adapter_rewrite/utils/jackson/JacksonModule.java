package nc.isi.fragaria_adapter_rewrite.utils.jackson;

import org.apache.tapestry5.ioc.ServiceBinder;

public class JacksonModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(EntityJsonDeserializerFactory.class);
		binder.bind(EntityBeanDeserializerModifier.class);
		binder.bind(EntityJacksonModule.class);
	}

}
