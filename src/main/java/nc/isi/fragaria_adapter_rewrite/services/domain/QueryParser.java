package nc.isi.fragaria_adapter_rewrite.services.domain;

public interface QueryParser<T> {

	public T parse(Query<?> query);

}
