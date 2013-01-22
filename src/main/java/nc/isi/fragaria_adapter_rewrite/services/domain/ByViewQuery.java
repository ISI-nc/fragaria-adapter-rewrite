package nc.isi.fragaria_adapter_rewrite.services.domain;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;
import java.util.Map.Entry;

import nc.isi.fragaria_adapter_rewrite.services.domain.GenericViews.All;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.CaseFormat;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.alias.Alias;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.Ops;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.PathBuilder;

/**
 * ViewQueryImpl predicate est prévu pour être utilisé via un {@link Alias} sans
 * spécification du path construit via {@code alias(Class<T> cl)}
 * 
 * La view devra avoir été créée dans la datasource //TODO: Générer les vues en
 * fonction des datasources et de critères prédéfinis
 * 
 * La fonction {@code filterBy()} sera utilisée sur la requête, chaque
 * {@link Entry} sera ajouté comme un and à la whereClause
 * 
 * Le predicate sera appliqué au résultat de la requête
 * 
 * @author jmaltat
 * 
 * @param <T>
 */
public class ByViewQuery<T extends Entity> implements Query<T> {
	private final Class<T> resultType;
	private final Class<? extends View> view;
	private final PathBuilder<T> entityPath;
	private BooleanBuilder builder;
	private final Map<String, Object> params = Maps.newHashMap();

	public ByViewQuery(Class<T> resultType, Class<? extends View> view) {
		this.view = view != null ? view : All.class;
		this.resultType = resultType;
		this.entityPath = new PathBuilder<>(resultType,
				CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
						resultType.getSimpleName()));
	}

	@Override
	public Class<T> getResultType() {
		return resultType;
	}

	public Class<? extends View> getView() {
		return view;
	}

	public ByViewQuery<T> filterBy(String key, Object value) {
		params.put(key, value);
		return this;
	}

	public ByViewQuery<T> filterBy(Map<String, Object> keyValues) {
		params.putAll(keyValues);
		return this;
	}

	public Map<String, Object> getFilter() {
		return params;
	}

	public ByViewQuery<T> where(String key, Object value) {
		return where(createPredicate(key, value));
	}

	public ByViewQuery<T> where(Predicate predicate) {
		checkState(builder == null,
				"where a déjà été appelé veuillez préciser l'association via or | and");
		builder = new BooleanBuilder(predicate);
		return this;
	}

	public ByViewQuery<T> and(String key, Object value) {
		return and(createPredicate(key, value));
	}

	public ByViewQuery<T> and(Predicate predicate) {
		checkState(builder != null, "Appelez where en premier");
		builder.and(predicate);
		return this;
	}

	public ByViewQuery<T> or(String key, Object value) {
		return or(createPredicate(key, value));
	}

	public ByViewQuery<T> or(Predicate predicate) {
		checkState(builder != null, "Appelez where en premier");
		builder.or(predicate);
		return this;

	}

	public Predicate getPredicate() {
		if (builder == null)
			return null;
		return builder.getValue();
	}

	private Predicate createPredicate(String key, Object value) {
		return Expressions.predicate(Ops.EQ, entityPath.get(key),
				Expressions.constant(value));
	}

}
