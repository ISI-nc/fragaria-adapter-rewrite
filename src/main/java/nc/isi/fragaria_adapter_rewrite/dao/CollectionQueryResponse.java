package nc.isi.fragaria_adapter_rewrite.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;

public class CollectionQueryResponse<T extends Entity> implements
		QueryResponse<T>, Iterable<T> {
	private final Collection<T> response;

	public CollectionQueryResponse(Collection<T> response) {
		checkNotNull(response);
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
