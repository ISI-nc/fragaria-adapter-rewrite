package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Collection;

public abstract class AbstractAdapter {

	protected <T extends Entity> CollectionQueryResponse<T> buildQueryResponse(
			Collection<T> collection) {
		for (Entity entity : collection) {
			entity.setState(State.COMMITED);
		}
		return new CollectionQueryResponse<>(collection);
	}

	protected <T extends Entity> UniqueQueryResponse<T> buildQueryResponse(
			T entity) {
		entity.setState(State.COMMITED);
		return new UniqueQueryResponse<T>(entity);
	}

	protected void commitError(Entity entity, State oldState, State state) {
		throw new RuntimeException(
				String.format(
						"Erreur sur l'état de l'objet %s, déjà enregistré avec l'état %s et demande à passer à %s ",
						entity, oldState, state));
	}

}
