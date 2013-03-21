package nc.isi.fragaria_adapter_rewrite.entities;

import junit.framework.TestCase;
import nc.isi.fragaria_adapter_rewrite.entities.ChangeSetTree.Branch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class TestChangeSetTree extends TestCase {

	private static final ObjectMapper objectMapper = FragariaObjectMapper.INSTANCE
			.get();

	public void testFork() throws Exception {
		ObjectNode json = objectMapper.createObjectNode();
		json.put("aKey", "aValue");

		ChangeSetTree<String> tree = new ChangeSetTree<>( //
				new ObjectNodeKVGet(json));

		assertNull(tree.get("test"));
		assertEquals("aValue", tree.get("aKey").asText());

		Branch<String> branch = tree.newBranch();

		branch.modify("test", new TextNode("test"));
		Branch<String> fork = branch.fork();
		branch.remove("test2");
		branch.modify("test3", new TextNode("test3"));

		fork.modify("test", new TextNode("test2"));
		fork.modify("test2", new TextNode("test2"));

		// Changes in branch are the right ones
		assertEquals("test", branch.get("test").asText());
		assertNull(branch.get("test2"));
		assertEquals("test3", branch.get("test3").asText());
		assertEquals("aValue", branch.get("aKey").asText());

		// Changes in the fork don't have the changes made after the fork.
		assertEquals("test2", fork.get("test").asText());
		assertEquals("test2", fork.get("test2").asText());
		assertNull(fork.get("test3"));
		assertEquals("aValue", fork.get("aKey").asText());
	}

}
