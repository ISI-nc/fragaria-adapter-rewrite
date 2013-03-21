package nc.isi.fragaria_adapter_rewrite.entities;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;

public class DelegateByChangeConflictSolver implements ConflictSolver {

	private Map<Object, ConflictSolver> knownSolutions = new TreeMap<>();

	/**
	 * Sets the solution for a specific key.
	 * 
	 * @param changedKey
	 *            The key that changed on both sides.
	 * @param delegate
	 *            The delegate to use for this specific key.
	 */
	public void setSolution(Object changedKey, ConflictSolver delegate) {
		knownSolutions.put(changedKey, delegate);
	}

	public void setModify(Object changedKey, JsonNode valueToSet) {
		setSolution(changedKey,
				new ModifyConflictSolver(changedKey, valueToSet));
	}

	public void setRemove(Object changedKey) {
		setSolution(changedKey, new RemoveValueConflictSolver(changedKey));
	}

	@Override
	public <K extends Comparable<? super K>> boolean canSolve(K changedKey,
			ChangeSet<K> ours, ChangeSet<K> theirs) {
		return knownSolutions.containsKey(changedKey);
	}

	@Override
	public <K extends Comparable<? super K>> void merge(K changedKey,
			ChangeSet<K> ours, ChangeSet<K> theirs) {
		knownSolutions.get(changedKey).merge(changedKey, ours, theirs);
	}

}
