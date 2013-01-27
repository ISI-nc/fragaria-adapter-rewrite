package nc.isi.fragaria_adapter_rewrite.utils;

import junit.framework.TestCase;

import org.junit.Test;

public class FileUtilsTest extends TestCase {
	private static final String FULL_FILE_NAME = "test.extension";
	private static final String FINAL_FILE_NAME = "test";

	@Test
	public void testRemoveExtension() {
		assertEquals("l'extension n'a pas été retirée correctement",
				FINAL_FILE_NAME, FileUtils.removeExtension(FULL_FILE_NAME));
	}

}
