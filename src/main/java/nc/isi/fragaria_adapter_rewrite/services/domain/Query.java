package nc.isi.fragaria_adapter_rewrite.services.domain;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.Constant;
import com.mysema.query.types.Ops;
import com.mysema.query.types.Path;
import com.mysema.query.types.Predicate;

public class Query<T extends Entity> {
	private final Class<T> type;
	private Class<? extends View> view;
	private final Path<T> entityPath;
	private BooleanBuilder builder;

	public Query(Class<T> type) {
		this.type = type;
		this.entityPath = Expressions.path(type, type.getSimpleName());
	}

	public Class<T> getType() {
		return type;
	}

	public Class<? extends View> getView() {
		return view;
	}

	public Query<T> where(String key, Object value) {
		Predicate predicate = createPredicate(key, value);
		builder = new BooleanBuilder(predicate);
		return this;
	}

	public Query<T> and(String key, Object value) {
		Predicate predicate = createPredicate(key, value);
		builder.and(predicate);
		return this;
	}

	public Query<T> or(String key, Object value) {
		Predicate predicate = createPredicate(key, value);
		builder.or(predicate);
		return this;
	}

	public Query<T> setView(Class<? extends View> view) {
		this.view = view;
		return this;
	}

	public Predicate getPredicate() {
		return builder.getValue();
	}

	private Predicate createPredicate(String key, Object value) {
		Path<Object> propertyPath = Expressions.path(Object.class, entityPath,
				key);
		Constant<?> constant = (Constant<?>) Expressions.constant(value);
		Predicate predicate = Expressions.predicate(Ops.EQ, constant,
				propertyPath);
		return predicate;
	}

}
