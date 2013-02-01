package nc.isi.fragaria_adapter_rewrite.utils.jackson;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

public class EntityBeanDeserializerModifier extends BeanDeserializerModifier {
	private final EntityJsonDeserializerFactory entityJsonDeserializerFactory = new EntityJsonDeserializerFactoryImpl();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nc.isi.fragaria_adapter_rewrite.utils.jackson.EntityBeanDeserializerModifier
	 * #modifyDeserializer(com.fasterxml.jackson.databind.DeserializationConfig,
	 * com.fasterxml.jackson.databind.BeanDescription,
	 * com.fasterxml.jackson.databind.JsonDeserializer)
	 */

	@SuppressWarnings("unchecked")
	@Override
	public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
			BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
		if (Entity.class.isAssignableFrom(beanDesc.getBeanClass())) {
			return entityJsonDeserializerFactory
					.create((Class<? extends Entity>) beanDesc.getBeanClass());
		}
		return super.modifyDeserializer(config, beanDesc, deserializer);
	}

}
