package nc.isi.fragaria_adapter_rewrite.utils.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class EntityJacksonModule extends Module {
	private final EntityBeanDeserializerModifier entityBeanDeserializerModifier;
	private final EntitySerializers entitySerializers;

	public EntityJacksonModule(
			EntityBeanDeserializerModifier entityBeanDeserializerModifier) {
		this.entityBeanDeserializerModifier = entityBeanDeserializerModifier;
		this.entitySerializers = new EntitySerializers();
	}

	@Override
	public String getModuleName() {
		return "EntityJackson";
	}

	@Override
	public Version version() {
		return new Version(0, 1, 0, "alpha", "nc.isi", "fragaria");
	}

	@Override
	public void setupModule(SetupContext context) {
		context.addBeanDeserializerModifier(entityBeanDeserializerModifier);
		context.addSerializers(entitySerializers);
	}

}
