package nc.isi.fragaria_adapter_rewrite.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class MergeConflictException extends Exception {
	private static final long serialVersionUID = 6827579801273073832L;

	private final Set<?> conflictingKeys;

	@SafeVarargs
	public <K extends Comparable<? super K>> MergeConflictException(
			K... conflictingKeys) {
		this(Arrays.asList(conflictingKeys));
	}

	public <K extends Comparable<? super K>> MergeConflictException(
			Collection<K> conflictingKeys) {
		super("Conflict on the following keys: " + conflictingKeys);
		this.conflictingKeys = new TreeSet<K>(conflictingKeys);
	}

	public Set<?> getConflictingKeys() {
		return conflictingKeys;
	}

}
