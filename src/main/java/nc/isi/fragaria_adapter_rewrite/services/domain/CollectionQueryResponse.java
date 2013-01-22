package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Collection;
import java.util.Iterator;

public class CollectionQueryResponse<T extends Entity> implements
		QueryResponse<T>, Iterable<T> {
	private final Collection<T> response;

	public CollectionQueryResponse(Collection<T> response) {
		this.response = response;
	}

	public Collection<T> getResponse() {
		return response;
	}

	@Override
	public Iterator<T> iterator() {
		return response.iterator();
	}

}
