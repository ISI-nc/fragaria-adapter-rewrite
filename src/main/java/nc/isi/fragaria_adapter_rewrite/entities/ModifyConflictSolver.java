package nc.isi.fragaria_adapter_rewrite.entities;

import com.fasterxml.jackson.databind.JsonNode;

public class ModifyConflictSolver implements ConflictSolver {

	private Object key;
	private JsonNode valueToSet;

	public ModifyConflictSolver(Object key, JsonNode valueToSet) {
		this.key = key;
		this.valueToSet = valueToSet;
	}

	@Override
	public <K extends Comparable<? super K>> boolean canSolve(K changedKey,
			ChangeSet<K> ours, ChangeSet<K> theirs) {
		return key.equals(changedKey);
	}

	@Override
	public <K extends Comparable<? super K>> void merge(K changedKey,
			ChangeSet<K> ours, ChangeSet<K> theirs) {
		ours.modify(changedKey, valueToSet);
	}

}
