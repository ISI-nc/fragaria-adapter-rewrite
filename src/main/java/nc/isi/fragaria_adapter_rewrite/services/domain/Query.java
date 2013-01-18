package nc.isi.fragaria_adapter_rewrite.services.domain;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.CaseFormat;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.alias.Alias;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.Ops;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.PathBuilder;

/**
 * Query predicate est prévu pour être utilisé via un {@link Alias} sans
 * spécification du path construit via {@code alias(Class<T> cl)}
 * 
 * @author jmaltat
 * 
 * @param <T>
 */
public class Query<T extends Entity> {
	private final Class<T> type;
	private Class<? extends View> view;
	private final PathBuilder<T> entityPath;
	private BooleanBuilder builder;

	public Query(Class<T> type) {
		this.type = type;
		this.entityPath = new PathBuilder<>(type, CaseFormat.UPPER_CAMEL.to(
				CaseFormat.LOWER_CAMEL, type.getSimpleName()));
	}

	public Class<T> getType() {
		return type;
	}

	public Class<? extends View> getView() {
		return view;
	}

	public Query<T> where(String key, Object value) {
		return where(createPredicate(key, value));
	}

	public Query<T> where(Predicate predicate) {
		checkState(builder == null,
				"where a déjà été appelé veuillez préciser l'association via or | and");
		builder = new BooleanBuilder(predicate);
		return this;
	}

	public Query<T> and(String key, Object value) {
		return and(createPredicate(key, value));
	}

	public Query<T> and(Predicate predicate) {
		checkState(builder != null, "Appelez where en premier");
		builder.and(predicate);
		return this;
	}

	public Query<T> or(String key, Object value) {
		return or(createPredicate(key, value));
	}

	public Query<T> or(Predicate predicate) {
		checkState(builder != null, "Appelez where en premier");
		builder.or(predicate);
		return this;

	}

	public Query<T> setView(Class<? extends View> view) {
		this.view = view;
		return this;
	}

	public Boolean hasView() {
		return view != null;
	}

	public Predicate getPredicate() {
		return builder.getValue();
	}

	private Predicate createPredicate(String key, Object value) {
		return Expressions.predicate(Ops.EQ, entityPath.get(key),
				Expressions.constant(value));
	}

}
