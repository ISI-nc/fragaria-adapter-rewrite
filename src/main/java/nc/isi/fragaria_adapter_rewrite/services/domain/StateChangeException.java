package nc.isi.fragaria_adapter_rewrite.services.domain;

public class StateChangeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -745130506869975174L;
	private final static String MESSAGE = "Impossible de passer de %s à %s";

	public StateChangeException(State oldState, State newState) {
		super(String.format(MESSAGE, oldState, newState));
	}

}
