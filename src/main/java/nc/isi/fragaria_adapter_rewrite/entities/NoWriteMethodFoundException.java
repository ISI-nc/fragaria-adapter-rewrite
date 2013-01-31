package nc.isi.fragaria_adapter_rewrite.entities;

public class NoWriteMethodFoundException extends RuntimeException {
	private static final String MESSAGE = "No write method was found for property %s in entity %s, this is mandatory use init() with @Final if u want a once write";

	public NoWriteMethodFoundException(Entity entity, String propertyName) {
		super(String.format(MESSAGE, propertyName, entity.getClass()));
	}

}
