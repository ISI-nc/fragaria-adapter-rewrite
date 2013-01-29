package nc.isi.fragaria_adapter_rewrite.entities;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ComplexObject extends AbstractEntity {
	private final String test;
	private final DateTime creation;
	private final String a;
	private final String b;

	public ComplexObject(ObjectNode objectNode, ObjectResolver objectResolver,
			EntityMetadataFactory entityMetadataFactory, String test,
			DateTime creation, String a, String b) {

		super(objectNode, objectResolver, entityMetadataFactory);
		this.test = test;
		this.creation = creation;
		this.a = a;
		this.b = b;
	}

	public String getTest() {
		return test;
	}

	public DateTime getCreation() {
		return creation;
	}

	public String getA() {
		return a;
	}

	public String getB() {
		return b;
	}

}
