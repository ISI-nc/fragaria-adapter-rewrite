package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ConnectionDataBuilderImpl implements ConnectionDataBuilder {

	private final Map<String, Class<? extends ConnectionData>> map;

	public ConnectionDataBuilderImpl(
			Map<String, Class<? extends ConnectionData>> map) {
		this.map = map;
	}

	@Override
	public ConnectionData build(String dsType, Object... params) {
		Class<?>[] paramClasses = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			paramClasses[i] = params[i].getClass();
		}
		try {
			Constructor<? extends ConnectionData> constructor = map.get(dsType)
					.getConstructor(paramClasses);
			return constructor.newInstance(params);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
