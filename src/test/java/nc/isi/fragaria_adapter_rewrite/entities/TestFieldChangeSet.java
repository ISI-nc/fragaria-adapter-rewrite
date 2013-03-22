package nc.isi.fragaria_adapter_rewrite.entities;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import com.fasterxml.jackson.databind.node.TextNode;

public class TestFieldChangeSet extends TestCase {

	// modify

	public void testModify() throws Exception {
		FieldChangeSet set = new FieldChangeSet();
		set.modify("test", new TextNode("test"));
		assertModifiedRemoved(set, "test", null);
	}

	// remote

	public void testRemove() throws Exception {
		FieldChangeSet set = new FieldChangeSet();
		set.remove("test");
		assertTrue(set.isRemoved("test"));
		assertFalse(set.isModified("test"));
		assertEquals(Collections.singleton("test"), set.changes());
		assertEquals(Collections.emptySet(), set.modifiedFields());
		assertEquals(Collections.singleton("test"), set.removedFields());
	}

	// get

	public void testGetModified() throws Exception {
		FieldChangeSet set = new FieldChangeSet();
		set.modify("test", new TextNode("test"));
		assertEquals("test", set.get("test").asText());
	}

	public void testGetRemoved() throws Exception {
		FieldChangeSet set = new FieldChangeSet();
		set.remove("test");
		assertNull(set.get("test"));
	}

	public void testGetUnchanged() throws Exception {
		FieldChangeSet set = new FieldChangeSet();
		try {
			set.get("test");
			fail("Exception not thrown");
		} catch (NoSuchElementException e) {
			// success
		}
	}

	// merge

	public void testMergeEmpty() throws Exception {
		FieldChangeSet set1 = new FieldChangeSet();
		FieldChangeSet set2 = new FieldChangeSet();

		set1.modify("test", new TextNode("test"));
		set1.mergeWith(set2);
		assertModifiedRemoved(set1, "test", null);

		set1 = new FieldChangeSet();
		set1.remove("test");
		assertModifiedRemoved(set1, null, "test");
	}

	public void testMergeModifiedRemoved() throws Exception {
		FieldChangeSet set1 = new FieldChangeSet();
		FieldChangeSet set2 = new FieldChangeSet();

		// M test + D test => ! test
		set1.modify("test", null);
		set2.remove("test");
		assertMergeFails(set1, set2, singleton("test"));

		// D test + M test => ! test
		set1 = new FieldChangeSet();
		set2 = new FieldChangeSet();
		set1.remove("test");
		set2.modify("test", null);
		assertMergeFails(set1, set2, singleton("test"));
	}

	public void testMergeConflict() throws Exception {
		FieldChangeSet set1 = new FieldChangeSet();
		FieldChangeSet set2 = new FieldChangeSet();

		// M test null + M test null => M test null
		set1.modify("test", null);
		set2.modify("test", null);
		set1.mergeWith(set2);
		assertModifiedRemoved(set1, "test", null);

		// D test "test1" + M test "test2" => MM test
		set1 = new FieldChangeSet();
		set1.modify("test", new TextNode("test1"));
		set2.modify("test", new TextNode("test2"));
		set2.remove("test2");

		// no auto-resolution => conflict
		assertMergeFails(set1, set2, singleton("test"));
		// and the change set hasn't been modified
		assertModifiedRemoved(set1, "test", null);
		assertEquals(new TextNode("test1"), set1.get("test"));

		// "ours" resolution
		set1.mergeWith(set2, ConflictSolver.OURS);
		assertModifiedRemoved(set1, "test", "test2");
		assertEquals(new TextNode("test1"), set1.get("test"));

		// "theirs" resolution
		set1.mergeWith(set2, ConflictSolver.THEIRS);
		assertModifiedRemoved(set1, "test", "test2");
		assertEquals(new TextNode("test2"), set1.get("test"));
	}

	// clear

	public void testClear() throws Exception {
		FieldChangeSet set = new FieldChangeSet();
		set.modify("test", new TextNode("test1"));
		set.remove("test2");
		assertModifiedRemoved(set, "test", "test2");

		set.clear();
		assertModifiedRemoved(set, (String) null, (String) null);
	}

	// freeze support

	public void testFreeze() throws Exception {
		FieldChangeSet set = new FieldChangeSet();
		set.freeze();
		try {
			set.modify("test", new TextNode("test"));
			fail("IllegalStateException not thrown");
		} catch (IllegalStateException e) {
			assertEquals("frozen", e.getMessage());
		}
		try {
			set.remove("test");
			fail("IllegalStateException not thrown");
		} catch (IllegalStateException e) {
			assertEquals("frozen", e.getMessage());
		}
		try {
			set.clear();
			fail("IllegalStateException not thrown");
		} catch (IllegalStateException e) {
			assertEquals("frozen", e.getMessage());
		}
		try {
			set.mergeWith(set);
			fail("IllegalStateException not thrown");
		} catch (IllegalStateException e) {
			assertEquals("frozen", e.getMessage());
		}
	}

	// utils

	private void assertMergeFails(FieldChangeSet set1, FieldChangeSet set2,
			Set<String> conflictingKeys) {
		try {
			set1.mergeWith(set2);
			fail("Exception not thrown");
		} catch (MergeConflictException e) {
			// The key in conflict is "test"
			assertEquals(conflictingKeys, e.getConflictingKeys());
		}
	}

	private void assertModifiedRemoved(FieldChangeSet set, String modified,
			String removed) {
		assertModifiedRemoved(set, singleValueToSet(modified),
				singleValueToSet(removed));
	}

	private Set<String> singleValueToSet(String value) {
		if (value == null) {
			return emptySet();
		} else {
			return singleton(value);
		}
	}

	private void assertModifiedRemoved(FieldChangeSet set,
			Set<String> modified, Set<String> removed) {
		for (String modifiedField : modified) {
			assertTrue(set.isModified(modifiedField));
			assertFalse(set.isRemoved(modifiedField));
		}

		Set<String> expectedChanges = new TreeSet<>();
		expectedChanges.addAll(modified);
		expectedChanges.addAll(removed);

		assertEquals(expectedChanges, set.changes());
		assertEquals(modified, set.modifiedFields());
		assertEquals(removed, set.removedFields());
	}
}
