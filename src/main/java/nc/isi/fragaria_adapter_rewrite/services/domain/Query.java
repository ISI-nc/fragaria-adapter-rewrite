package nc.isi.fragaria_adapter_rewrite.services.domain;

public interface Query<T extends Entity> {

	public Class<T> getResultType();

}