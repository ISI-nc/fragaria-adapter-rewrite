package nc.isi.fragaria_adapter_rewrite.resources;

import java.util.Collection;
import java.util.Map;


import com.google.common.collect.Maps;

public class MasterDsLoaderImpl implements MasterDsLoader {

	private final Map<String, Datasource> map = Maps.newHashMap();;

	public MasterDsLoaderImpl(Collection<SpecificDsLoader> loaders) {
		for (SpecificDsLoader loader : loaders)
			map.putAll(loader.getDs());
	}

	@Override
	public Map<String, Datasource> getDs() {
		return map;
	}

}
