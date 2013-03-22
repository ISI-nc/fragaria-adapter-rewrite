package nc.isi.fragaria_adapter_rewrite.entities;

public class RemoveValueConflictSolver implements ConflictSolver {

	private Object key;

	public RemoveValueConflictSolver(Object key) {
		this.key = key;
	}

	@Override
	public <K extends Comparable<? super K>> boolean canSolve(K changedKey,
			ChangeSet<K> ours, ChangeSet<K> theirs) {
		return key.equals(changedKey);
	}

	@Override
	public <K extends Comparable<? super K>> void merge(K changedKey,
			ChangeSet<K> ours, ChangeSet<K> theirs) {
		ours.remove(changedKey);
	}

}
