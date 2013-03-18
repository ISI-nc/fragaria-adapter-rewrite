package nc.isi.fragaria_adapter_rewrite.entities;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Classe représentant un ensemble de changements.
 * 
 * @author Mikaël Cluseau
 * 
 */
public class FieldChangeSet {

	private static final ObjectMapper OBJECT_MAPPER = FragariaObjectMapper.INSTANCE
			.get();

	private final ObjectNode modifications = OBJECT_MAPPER.createObjectNode();

	private final Set<String> deletions = new TreeSet<>();

	// ------------------------------------------------------------------
	// Status methods
	//

	/**
	 * @param fieldName
	 *            The name of the field we're looking for.
	 * @return <code>true</code> iff <code>fieldName</code> is modified or
	 *         removed.
	 */
	public boolean isChanged(String fieldName) {
		return modifications.has(fieldName) || deletions.contains(fieldName);
	}

	/**
	 * @return All changed field (modified of removed).
	 */
	public Set<String> changes() {
		Set<String> changes = new TreeSet<>();
		changes.addAll(modifiedFields());
		changes.addAll(removedFields());
		return changes;
	}

	/**
	 * @param fieldName
	 *            The name of the field we're looking for.
	 * @return <code>true</code> iff it has been modified (not removed).
	 */
	public boolean isModified(String fieldName) {
		return modifications.has(fieldName);
	}

	/**
	 * @return All modified fields.
	 */
	public Set<String> modifiedFields() {
		Set<String> modifiedFields = new TreeSet<>();
		for (Iterator<String> it = modifications.fieldNames(); it.hasNext();) {
			modifiedFields.add(it.next());
		}
		return Collections.unmodifiableSet(modifiedFields);
	}

	/**
	 * 
	 * @param fieldName
	 *            The name of the field we're looking for.
	 * @return <code>true</code> iff it has been removed (not modified).
	 */
	public boolean isRemoved(String fieldName) {
		return deletions.contains(fieldName);
	}

	/**
	 * @return All removed fields.
	 */
	public Set<String> removedFields() {
		return Collections.unmodifiableSet(deletions);
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
	public JsonNode get(String fieldName) {
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
	public void modify(String fieldName, JsonNode newValue) {
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
	public void remove(String fieldName) {
		deletions.add(fieldName);
		// Si cette valeur était modifiée, elle n'est plus.
		if (modifications.has(fieldName)) {
			modifications.remove(fieldName);
		}
	}

	// ------------------------------------------------------------------
	// ChangeSet level methods
	//

	/**
	 * Merges the changes from another {@link FieldChangeSet}.
	 * 
	 * @param other
	 *            The {@link FieldChangeSet} to merge with.
	 */
	public void mergeWith(FieldChangeSet other) throws MergeConflictException {
		mergeWith(other, ConflictResolution.FAIL);
	}

	/**
	 * Merges the changes from another {@link FieldChangeSet}.
	 * 
	 * @param other
	 *            The {@link FieldChangeSet} to merge with.
	 * @param resolution
	 *            The resolution to apply in the event of a conflict.
	 */
	public void mergeWith(FieldChangeSet other, ConflictResolution resolution)
			throws MergeConflictException {
		Set<String> modifiedFields = other.modifiedFields();
		// Look for merge conflicts
		if (resolution == ConflictResolution.FAIL) {
			Set<String> conflictingKeys = new TreeSet<>();
			for (String modifiedField : modifiedFields) {
				if (isModified(modifiedField)) {
					conflictingKeys.add(modifiedField);
				}
			}
			if (!conflictingKeys.isEmpty()) {
				throw new MergeConflictException(conflictingKeys);
			}
		}
		// Merge modifications
		for (String modifiedField : modifiedFields) {
			if (isModified(modifiedField)) {
				// Conflict
				switch (resolution) {
				case OURS:
					// ignore other's change
					break;
				case THEIRS:
					modify(modifiedField, other.get(modifiedField));
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
		for (String removedField : other.removedFields()) {
			remove(removedField);
		}
	}

}
