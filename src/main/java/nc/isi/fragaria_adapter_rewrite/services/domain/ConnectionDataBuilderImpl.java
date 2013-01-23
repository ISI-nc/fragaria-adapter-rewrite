package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ConnectionDataBuilderImpl implements ConnectionDataBuilder {

	private final Map<String, Class<? extends ConnectionData>> map;

	public ConnectionDataBuilderImpl(
			Map<String, Class<? extends ConnectionData>> map) {
		this.map = map;
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
