package nc.isi.fragaria_adapter_rewrite.services.domain;

public class DeleteOperation implements Operation {

	private final Entity deletedEntity;

	public DeleteOperation(Entity deletedEntity) {
		this.deletedEntity = deletedEntity;
	}

	@Override
	public OperationType getType() {
		return OperationType.DELETE;
	}

}
