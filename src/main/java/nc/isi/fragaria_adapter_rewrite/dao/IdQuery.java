package nc.isi.fragaria_adapter_rewrite.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;

import com.mysema.query.BooleanBuilder;

/**
 * Utilisée pour faire une requête par ID, on peut ajouter la REV pour récupérer
 * la valeur d'une information à un instant précis
 * 
 * @author jmaltat
 * 
 * @param <T>
 */
public class IdQuery<T extends Entity> extends AbstractQuery<T> {
	private final String id;
	private String rev;

	public IdQuery(Class<T> resultType, String id) {
		super(resultType);
		checkNotNull(id);
		this.id = id;
		builder = initBuilder(id);
	}

	protected BooleanBuilder initBuilder(String id) {
		return new BooleanBuilder(createPredicate(Entity.ID, id));
	}

	public String getRev() {
		return rev;
	}

	public IdQuery<T> addRev(String rev) {
		checkState(this.rev == null);
		checkNotNull(rev);
		builder.and(createPredicate(Entity.REV, rev));
		this.rev = rev;
		return this;
	}

	public String getId() {
		return id;
	}

}
