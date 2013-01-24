package nc.isi.fragaria_adapter_rewrite.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;

import com.google.common.base.CaseFormat;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.Ops;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.PathBuilder;

public abstract class AbstractQuery<T extends Entity> implements Query<T> {
	private final PathBuilder<T> entityPath;
	private final Class<T> resultType;
	protected BooleanBuilder builder;

	public AbstractQuery(Class<T> resultType) {
		checkNotNull(resultType);
		this.resultType = resultType;
		this.entityPath = new PathBuilder<>(resultType,
				CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
						resultType.getSimpleName()));
	}

	@Override
	public Class<T> getResultType() {
		return resultType;
	}

	public Predicate getPredicate() {
		if (builder == null)
			return null;
		return builder.getValue();
	}

	protected Predicate createPredicate(String key, Object value) {
		checkNotNull(key);
		return Expressions.predicate(Ops.EQ, entityPath.get(key),
				value == null ? null : Expressions.constant(value));
	}
}
