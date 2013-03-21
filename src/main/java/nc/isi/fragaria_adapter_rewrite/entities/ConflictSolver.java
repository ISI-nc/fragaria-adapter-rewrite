package nc.isi.fragaria_adapter_rewrite.entities;

public interface ConflictSolver {

	/**
	 * The classical "OURS" strategy.
	 * 
	 * Ignores the changes in "theirs".
	 */
	public static final ConflictSolver OURS = new ConflictSolver() {
		@Override
		public <K extends Comparable<? super K>> boolean canSolve(K changedKey,
				ChangeSet<K> ours, ChangeSet<K> theirs) {
			return true;
		}

		@Override
		public <K extends Comparable<? super K>> void merge(K changedKey,
				ChangeSet<K> ours, ChangeSet<K> theirs) {
			// Ignore theirs' change
		}
	};

	/**
	 * The classical "THEIRS" strategy.
	 * 
	 * Overwrites changes in "ours" by those in "theirs".
	 */
	public static final ConflictSolver THEIRS = new ConflictSolver() {
		@Override
		public <K extends Comparable<? super K>> boolean canSolve(K changedKey,
				ChangeSet<K> ours, ChangeSet<K> theirs) {
			return true;
		}

		@Override
		public <K extends Comparable<? super K>> void merge(K changedKey,
				ChangeSet<K> ours, ChangeSet<K> theirs) {
			// Overwrite our change
			if (theirs.isRemoved(changedKey)) {
				ours.remove(changedKey);
			} else {
				ours.modify(changedKey, theirs.get(changedKey));
			}
		}
	};

	/**
	 * A good default: doesn't solve anything (canSolve returns false).
	 */
	public static final ConflictSolver FAIL = new ConflictSolver() {
		@Override
		public <K extends Comparable<? super K>> boolean canSolve(K changedKey,
				ChangeSet<K> ours, ChangeSet<K> theirs) {
			return false;
		}

		@Override
		public <K extends Comparable<? super K>> void merge(K changedKey,
				ChangeSet<K> ours, ChangeSet<K> theirs) {
			throw new UnsupportedOperationException();
		}
	};

	/**
	 * Indicate if we know how to solve the given conflict.
	 * 
	 * @param changedKey
	 *            The key that changed on both sides.
	 * @param ours
	 *            The ChangeSet to merge to.
	 * @param theirs
	 *            The ChangeSet to merge from.
	 * @return <code>true</code> iff
	 *         {@link #merge(Comparable, ChangeSet, ChangeSet)} will succeed.
	 */
	public <K extends Comparable<? super K>> boolean canSolve(K changedKey,
			ChangeSet<K> ours, ChangeSet<K> theirs);

	/**
	 * Merge the given conflict.
	 * 
	 * @param changedKey
	 *            The key that changed on both sides.
	 * @param ours
	 *            The ChangeSet to merge to.
	 * @param theirs
	 *            The ChangeSet to merge from.
	 */
	public <K extends Comparable<? super K>> void merge(K changedKey,
			ChangeSet<K> ours, ChangeSet<K> theirs);

}
