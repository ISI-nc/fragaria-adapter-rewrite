package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.Map;

import org.testng.collections.Maps;

public class Query<T extends Entity> {
	private final Map<String, Object> params = Maps.newHashMap();
	private final Class<T> type;
	private Class<? extends View> view;

	public Query(Class<T> type) {
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public Class<? extends View> getView() {
		return view;
	}

	public Query<T> put(String key, Object value) {
		params.put(key, value);
		return this;
	}

	public Query<T> setView(Class<? extends View> view) {
		this.view = view;
		return this;
	}

}
