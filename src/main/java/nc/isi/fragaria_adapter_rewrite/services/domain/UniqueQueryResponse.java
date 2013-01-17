package nc.isi.fragaria_adapter_rewrite.services.domain;

public class UniqueQueryResponse<T extends Entity> implements QueryResponse<T> {
	private final T response;

	public UniqueQueryResponse(T response) {
		this.response = response;
	}

	public T getResponse() {
		return response;
	}

}
