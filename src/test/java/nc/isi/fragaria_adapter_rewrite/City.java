package nc.isi.fragaria_adapter_rewrite;

import nc.isi.fragaria_adapter_rewrite.CityViews.Name;
import nc.isi.fragaria_adapter_rewrite.services.domain.AbstractEntity;
import nc.isi.fragaria_adapter_rewrite.services.domain.DsKey;
import nc.isi.fragaria_adapter_rewrite.services.domain.EntityMetadataFactory;
import nc.isi.fragaria_adapter_rewrite.services.domain.ObjectResolver;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.node.ObjectNode;

@DsKey("rer")
public class City extends AbstractEntity {
	public static final String NAME = "name";

	public City(ObjectNode objectNode, ObjectResolver objectResolver,
			EntityMetadataFactory entityMetadataFactory) {
		super(objectNode, objectResolver, entityMetadataFactory);
	}

	@JsonView(Name.class)
	public String getName() {
		return readProperty(String.class, NAME);
	}

	public void setName(String name) {
		writeProperty(NAME, name);
	}

	@Override
	public String toString() {
		return toJSON().toString();
	}

}
