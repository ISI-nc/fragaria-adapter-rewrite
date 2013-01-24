package nc.isi.fragaria_adapter_rewrite.dao.adapters;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.dao.CollectionQueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.UniqueQueryResponse;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.enums.State;

public abstract class AbstractAdapter {

	protected <T extends Entity> CollectionQueryResponse<T> buildQueryResponse(
			Collection<T> collection) {
		return new CollectionQueryResponse<>(collection);
	}

	protected <T extends Entity> UniqueQueryResponse<T> buildQueryResponse(
			T entity) {
		return new UniqueQueryResponse<T>(entity);
	}

	protected void commitError(Entity entity, State oldState, State state) {
		throw new RuntimeException(
				String.format(
						"Erreur sur l'état de l'objet %s, déjà enregistré avec l'état %s et demande à passer à %s ",
						entity, oldState, state));
	}

}
