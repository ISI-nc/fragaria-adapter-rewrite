package nc.isi.fragaria_adapter_rewrite.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class MergeConflictException extends Exception {
	private static final long serialVersionUID = 6827579801273073832L;

	private final Set<String> conflictingKeys;

	public MergeConflictException(String... conflictingKeys) {
		this(Arrays.asList(conflictingKeys));
	}

	public MergeConflictException(Collection<String> conflictingKeys) {
		super("Conflict on the following keys: " + conflictingKeys);
		this.conflictingKeys = new TreeSet<>(conflictingKeys);
	}

	public Set<String> getConflictingKeys() {
		return conflictingKeys;
	}

}
