package nc.isi.fragaria_adapter_rewrite;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.PersonViews.NameView;
import nc.isi.fragaria_adapter_rewrite.services.domain.AbstractEntity;
import nc.isi.fragaria_adapter_rewrite.services.domain.EntityMetadataFactory;
import nc.isi.fragaria_adapter_rewrite.services.domain.ObjectResolver;
import nc.isi.fragaria_adapter_rewrite.services.domain.BackReference;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PersonData extends AbstractEntity {

	public PersonData(ObjectNode objectNode, ObjectResolver objectResolver,
			EntityMetadataFactory entityMetadataFactory) {
		super(objectNode, objectResolver, entityMetadataFactory);
	}

	@JsonView(NameView.class)
	public String getName() {
		return null;
	}

	public Patient getPatient() {
		return null;
	}

	@BackReference
	public Collection<Patient> getPatients() {
		return null;
	}

}
