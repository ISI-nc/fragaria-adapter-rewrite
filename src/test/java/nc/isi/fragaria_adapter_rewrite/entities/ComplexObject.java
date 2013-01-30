package nc.isi.fragaria_adapter_rewrite.entities;

import nc.isi.fragaria_adapter_rewrite.annotations.DsKey;

import org.joda.time.DateTime;

@DsKey("test")
public class ComplexObject extends AbstractEntity {
	private final String test;
	private final DateTime creation;
	private final String a;
	private final String b;

	public ComplexObject(String test, DateTime creation, String a, String b) {
		super();
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
