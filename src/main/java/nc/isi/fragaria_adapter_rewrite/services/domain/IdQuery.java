package nc.isi.fragaria_adapter_rewrite.services.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilisée pour faire une requête par ID, on peut ajouter la REV pour récupérer
 * la valeur d'une information à un instant précis
 * 
 * @author jmaltat
 * 
 * @param <T>
 */
public class IdQuery<T extends Entity> implements Query<T> {
	private final Class<T> resultType;
	private final String id;
	private String rev;

	public IdQuery(Class<T> resultType, String id) {
		checkNotNull(resultType);
		checkNotNull(id);
		this.id = id;
		this.resultType = resultType;
	}

	@Override
	public Class<T> getResultType() {
		return resultType;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public String getId() {
		return id;
	}

}
