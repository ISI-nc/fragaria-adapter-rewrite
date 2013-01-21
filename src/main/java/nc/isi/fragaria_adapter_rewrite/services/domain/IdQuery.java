package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.UUID;

/**
 * Utilisée pour faire une requête par ID, on peut ajouter la REV pour récupérer la valeur
 * d'une information à un instant précis
 * @author jmaltat
 *
 * @param <T>
 */
public class IdQuery<T extends Entity> implements Query<T> {
	private final Class<T> resultType;
	private final UUID id;
	private UUID rev;
	
	public IdQuery(Class<T> resultType, UUID id){
		this.id = id;
		this.resultType = resultType;
	}

	@Override
	public Class<T> getResultType() {
		return resultType;
	}

	public UUID getRev() {
		return rev;
	}

	public void setRev(UUID rev) {
		this.rev = rev;
	}

	public UUID getId() {
		return id;
	}

}
