package nc.isi.fragaria_adapter_rewrite.services.domain;

public interface ConnectionDataBuilder {

	public ConnectionData build(String dsType, Object... params);

}