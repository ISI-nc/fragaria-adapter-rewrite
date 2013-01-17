package nc.isi.fragaria_adapter_rewrite;

import nc.isi.fragaria_adapter_rewrite.services.domain.EntityMetadataFactory;
import nc.isi.fragaria_adapter_rewrite.services.domain.ObjectResolver;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Patient extends PersonData {

	public Patient(ObjectNode objectNode, ObjectResolver objectResolver,
			EntityMetadataFactory entityMetadataFactory) {
		super(objectNode, objectResolver, entityMetadataFactory);
	}

}
