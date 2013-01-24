package nc.isi.fragaria_adapter_rewrite.resources;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

public class ConnectionDataBuilderImpl implements ConnectionDataBuilder {

	private final Map<String, Class<? extends ConnectionData>> map;

	public ConnectionDataBuilderImpl(Map<String, String> map) {
		this.map = convert(map);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Class<? extends ConnectionData>> convert(
			Map<String, String> map) {
		Map<String, Class<? extends ConnectionData>> buildMap = Maps
				.newHashMap();
		try {
			for (Entry<String, String> entry : map.entrySet()) {
				buildMap.put(entry.getKey(),
						(Class<? extends ConnectionData>) Class.forName(entry
								.getValue()));
			}
			return buildMap;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ConnectionData build(String dsType, Collection<Object> params) {
		Class<?>[] paramClasses = new Class<?>[params.size()];
		Object[] paramsTable = new Object[params.size()];
		int i = 0;
		for (Object param : params) {
			paramClasses[i] = param.getClass();
			paramsTable[i] = param;
			i++;
		}

		try {
			Constructor<? extends ConnectionData> constructor = map.get(dsType)
					.getConstructor(paramClasses);
			return constructor.newInstance(paramsTable);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public ConnectionData build(String dsType, Object... params) {
		return build(dsType, Arrays.asList(params));
	}

}
