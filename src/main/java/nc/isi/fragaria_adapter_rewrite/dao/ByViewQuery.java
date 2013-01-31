package nc.isi.fragaria_adapter_rewrite.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericQueryViews.All;
import nc.isi.fragaria_adapter_rewrite.entities.views.View;

import com.google.common.collect.Maps;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.alias.Alias;
import com.mysema.query.types.Predicate;

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
public class ByViewQuery<T extends Entity> extends AbstractQuery<T> {
	private final Class<? extends View> view;
	private final Map<String, Object> params = Maps.newHashMap();

	public ByViewQuery(Class<T> resultType, Class<? extends View> view) {
		super(resultType);
		this.view = view != null ? view : All.class;
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
		checkState(getBuilder() == null,
				"where a déjà été appelé veuillez préciser l'association via or | and");
		checkNotNull(predicate);
		setBuilder(new BooleanBuilder(predicate));
		return this;
	}

	public ByViewQuery<T> and(String key, Object value) {
		return and(createPredicate(key, value));
	}

	public ByViewQuery<T> and(Predicate predicate) {
		checkAddPredicate(predicate);
		getBuilder().and(predicate);
		return this;
	}

	public ByViewQuery<T> or(String key, Object value) {
		return or(createPredicate(key, value));
	}

	public ByViewQuery<T> or(Predicate predicate) {
		checkAddPredicate(predicate);
		getBuilder().or(predicate);
		return this;

	}

	protected void checkAddPredicate(Predicate predicate) {
		checkState(getBuilder() != null, "Appelez where en premier");
		checkNotNull(predicate);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("for class : " + getResultType() + "\n");
		sb.append("with view : " + getView() + "\n");
		sb.append("where filter : " + getFilter() + "\n");
		sb.append("and predicate : " + getPredicate());
		return sb.toString();
	}

}
