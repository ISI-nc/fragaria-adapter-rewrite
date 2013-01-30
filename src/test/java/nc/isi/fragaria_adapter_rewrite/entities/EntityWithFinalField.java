package nc.isi.fragaria_adapter_rewrite.entities;

import nc.isi.fragaria_adapter_rewrite.annotations.DsKey;
import nc.isi.fragaria_adapter_rewrite.annotations.Final;

@DsKey("test")
public class EntityWithFinalField extends AbstractEntity {
	public static final String TEST = "test";

	@Final
	public void setTest(String test) {
		init(TEST, test);
	}

	public String getTest() {
		return readProperty(String.class, TEST);
	}

}
