package nc.isi.fragaria_adapter_rewrite.entities;

import static junit.framework.Assert.assertNotNull;

import org.junit.Test;

public class TestFinalAnnotation {

	@Test
	public void test() {
		IllegalStateException illegalStateException = null;
		EntityWithFinalField entityWithFinalField = new EntityWithFinalField();
		entityWithFinalField.setTest("test");
		try {
			entityWithFinalField.setTest("test2");
		} catch (IllegalStateException e) {
			illegalStateException = e;
		}
		assertNotNull(illegalStateException);
	}

}
