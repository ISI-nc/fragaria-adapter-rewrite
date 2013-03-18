package nc.isi.fragaria_adapter_rewrite.entities;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Classe représentant un ensemble de changements.
 * 
 * @author Mikaël Cluseau
 * 
 */
public class ChangeSet<K extends Comparable<? super K>> {

	private final Map<K, JsonNode> modifications = new TreeMap<>();

	private final Set<K> deletions = new TreeSet<>();

	private boolean frozen = false;

	// ------------------------------------------------------------------
	// Status methods
	//

	/**
	 * @param fieldName
	 *            The name of the field we're looking for.
	 * @return <code>true</code> iff <code>fieldName</code> is modified or
	 *         removed.
	 */
	public boolean isChanged(K fieldName) {
		return isModified(fieldName) || isRemoved(fieldName);
	}

	/**
	 * @return All changed field (modified of removed).
	 */
	public Set<K> changes() {
		Set<K> changes = new TreeSet<>();
		changes.addAll(modifiedFields());
		changes.addAll(removedFields());
		return changes;
	}

	/**
	 * @param fieldName
	 *            The name of the field we're looking for.
	 * @return <code>true</code> iff it has been modified (not removed).
	 */
	public boolean isModified(K fieldName) {
		return modifications.containsKey(fieldName);
	}

	/**
	 * @return All modified fields.
	 */
	public Set<K> modifiedFields() {
		return Collections.unmodifiableSet(modifications.keySet());
	}

	/**
	 * 
	 * @param fieldName
	 *            The name of the field we're looking for.
	 * @return <code>true</code> iff it has been removed (not modified).
	 */
	public boolean isRemoved(K fieldName) {
		return deletions.contains(fieldName);
	}

	/**
	 * @return All removed fields.
	 */
	public Set<K> removedFields() {
		return Collections.unmodifiableSet(deletions);
	}

	/**
	 * @return <code>true</code> iff this ChangeSet is empty.
	 */
	public boolean isEmpty() {
		return modifications.isEmpty() && deletions.isEmpty();
	}

	// ------------------------------------------------------------------
	// State change methods
	//

	/**
	 * Retrieves the modified value for a field.
	 * 
	 * @param fieldName
	 *            The name of the field we're looking for.
	 * @return The new value for this field.
	 */
	public JsonNode get(K fieldName) {
		if (isModified(fieldName)) {
			return modifications.get(fieldName);
		} else if (isRemoved(fieldName)) {
			return null;
		} else {
			throw new NoSuchElementException("Field " + fieldName
					+ " has not changed.");
		}
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
		failIfFrozen();
		
		modifications.put(fieldName, newValue);
		// Si cette valeur était supprimée, elle ne l'est plus.
		deletions.remove(fieldName);
	}

	/**
	 * Records the removal of a field.
	 * 
	 * @param fieldName
	 *            The removed field.
	 */
	public void remove(K fieldName) {
		failIfFrozen();
		
		deletions.add(fieldName);
		// Si cette valeur était modifiée, elle n'est plus.
		if (modifications.containsKey(fieldName)) {
			modifications.remove(fieldName);
		}
	}

	/**
	 * Clear the changes recorded by this change set.
	 */
	public void clear() {
		failIfFrozen();
		
		modifications.clear();
		deletions.clear();
	}

	public void freeze() {
		frozen = true;
	}

	public void unfreeze() {
		frozen = false;
	}

	protected void failIfFrozen() {
		if (frozen) {
			throw new IllegalStateException("frozen");
		}
	}

	// ------------------------------------------------------------------
	// ChangeSet level methods
	//

	/**
	 * Merges the changes from another {@link ChangeSet}.
	 * 
	 * @param other
	 *            The {@link ChangeSet} to merge with.
	 */
	public void mergeWith(ChangeSet<K> other) throws MergeConflictException {
		mergeWith(other, ConflictResolution.FAIL);
	}

	/**
	 * Merges the changes from another {@link ChangeSet}.
	 * 
	 * @param other
	 *            The {@link ChangeSet} to merge with.
	 * @param resolution
	 *            The resolution to apply in the event of a conflict.
	 */
	public void mergeWith(ChangeSet<K> other, ConflictResolution resolution)
			throws MergeConflictException {
		failIfFrozen();

		Set<K> modifiedFields = other.modifiedFields();
		// Look for merge conflicts
		if (resolution == ConflictResolution.FAIL) {
			Set<K> conflictingKeys = new TreeSet<>();
			for (K modifiedField : modifiedFields) {
				if (!isModified(modifiedField)) {
					continue;
				}
				JsonNode mine = get(modifiedField);
				JsonNode their = other.get(modifiedField);
				if (nullSafeEquals(mine, their)) {
					continue; // modified by the same value => no conflict
				}
				conflictingKeys.add(modifiedField);
			}
			if (!conflictingKeys.isEmpty()) {
				throw new MergeConflictException(conflictingKeys);
			}
		}
		// Merge modifications
		for (K modifiedField : modifiedFields) {
			if (isModified(modifiedField)) {
				JsonNode mine = get(modifiedField);
				JsonNode their = other.get(modifiedField);
				if (nullSafeEquals(mine, their)) {
					continue; // modified by the same value => ignore
				}
				// Conflict
				switch (resolution) {
				case OURS:
					// ignore other's change
					break;
				case THEIRS:
					// overwrite our change
					modify(modifiedField, their);
					break;
				case FAIL:
					throw new IllegalStateException("Unreachable code");
				default:
					throw new IllegalArgumentException("Unknown resolution: "
							+ resolution);
				}
			} else if (isRemoved(modifiedField)) {
				// removed on our side, so ignore the other's change
			} else {
				modify(modifiedField, other.get(modifiedField));
			}
		}
		// Merge removals
		for (K removedField : other.removedFields()) {
			remove(removedField);
		}
	}

	private boolean nullSafeEquals(Object mine, Object their) {
		return mine == null ? their == null : mine.equals(their);
	}

}
