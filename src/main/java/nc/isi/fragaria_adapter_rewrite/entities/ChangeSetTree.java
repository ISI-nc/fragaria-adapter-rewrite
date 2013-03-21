package nc.isi.fragaria_adapter_rewrite.entities;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public class ChangeSetTree<K extends Comparable<? super K>> implements
		KeyValueGet<K, JsonNode> {

	public static class Branch<K extends Comparable<? super K>> implements
			KeyValueGet<K, JsonNode> {
		private Node<K> head;
		private Node<K> base;

		public Branch(Node<K> head) {
			this.head = head;
			this.base = head.parent;
		}

		public Branch<K> fork() {
			Node<K> child1 = head.newChild();
			Node<K> child2 = head.newChild();
			head = child1;
			return new Branch<K>(child2);
		}

		public JsonNode get(K key) {
			return head.get(key);
		}

		/**
		 * Records the modification of a field.
		 * 
		 * @param fieldName
		 *            The name of the modified field.
		 * @param newValue
		 *            The new value set.
		 */
		public void modify(K fieldName, JsonNode newValue) {
			head.getChangeSet().modify(fieldName, newValue);
		}

		/**
		 * Records the removal of a field.
		 * 
		 * @param fieldName
		 *            The removed field.
		 */
		public void remove(K fieldName) {
			head.getChangeSet().remove(fieldName);
		}

		public ChangeSet<K> getBranchChangeSet() {
			ChangeSet<K> branchChangeSet = new ChangeSet<>();
			Node<K> current = head;
			do {
				try {
					branchChangeSet.mergeWith(current.getChangeSet(),
							ConflictSolver.OURS);
				} catch (MergeConflictException e) {
					throw new IllegalStateException("unreachable");
				}
				current = current.getParent();
			} while (current != base);
			return branchChangeSet;
		}

		public ChangeSet<K> getFullChangeSet() {
			ChangeSet<K> fullChangeSet = new ChangeSet<>();
			for (Node<K> current = head; current != null; current = current
					.getParent()) {
				try {
					fullChangeSet.mergeWith(current.getChangeSet(),
							ConflictSolver.OURS);
				} catch (MergeConflictException e) {
					throw new IllegalStateException("unreachable");
				}
			}
			return fullChangeSet;
		}

	}

	public static class Node<K extends Comparable<? super K>> implements
			KeyValueGet<K, JsonNode> {

		private final Node<K> parent;

		private final Set<SoftReference<Node<K>>> children = new HashSet<>();

		private final ChangeSet<K> changeSet = new ChangeSet<>();

		private final ChangeSetTree<K> container;

		public Node(Node<K> parent, ChangeSetTree<K> container) {
			this.parent = parent;
			this.container = container;
		}

		public JsonNode get(K key) {
			if (changeSet.isChanged(key)) {
				return changeSet.get(key);
			}
			if (parent != null) {
				return parent.get(key);
			}
			return container.get(key);
		}

		public ChangeSet<K> getChangeSet() {
			return changeSet;
		}

		public Node<K> getParent() {
			return parent;
		}

		public Set<Node<K>> getChildren() {
			// good time for a cleanup
			cleanupChildren();
			// and the reald work
			Set<Node<K>> children = new HashSet<>(this.children.size());
			for (SoftReference<Node<K>> ref : this.children) {
				Node<K> child = ref.get();
				if (child == null) {
					continue;
				}
				children.add(ref.get());
			}
			return children;
		}

		private void cleanupChildren() {
			List<SoftReference<Node<K>>> refsToRemove = new LinkedList<>();
			for (SoftReference<Node<K>> ref : children) {
				if (ref.get() != null) {
					continue;
				}
				refsToRemove.add(ref);
			}
			children.removeAll(refsToRemove);
		}

		public Node<K> newChild() {
			Node<K> node = new Node<K>(this, container);
			children.add(new SoftReference<ChangeSetTree.Node<K>>(node));
			return node;
		}

	}

	private KeyValueGet<K, JsonNode> delegate;

	public ChangeSetTree() {
		this(null);
	}

	public ChangeSetTree(KeyValueGet<K, JsonNode> delegate) {
		this.delegate = delegate;
	}

	public JsonNode get(K key) {
		if (delegate == null) {
			throw new NoSuchElementException(String.valueOf(key));
		}
		return delegate.get(key);
	}

	public Branch<K> newBranch() {
		return new Branch<>(new Node<K>(null, this));
	}

}
