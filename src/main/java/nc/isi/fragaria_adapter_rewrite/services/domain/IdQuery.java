package nc.isi.fragaria_adapter_rewrite.services.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mysema.query.BooleanBuilder;

/**
 * Utilisée pour faire une requête par ID, on peut ajouter la REV pour récupérer
 * la valeur d'une information à un instant précis
 * 
 * @author jmaltat
 * 
 * @param <T>
 */
public class IdQuery<T extends Entity> extends AbstractQuery<T> implements
		Query<T> {
	private final String id;
	private String rev;

	public IdQuery(Class<T> resultType, String id) {
		checkNotNull(resultType);
		checkNotNull(id);
		super(resultType);
		this.id = id;
		builder = new BooleanBuilder(createPredicate("id", id));
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
