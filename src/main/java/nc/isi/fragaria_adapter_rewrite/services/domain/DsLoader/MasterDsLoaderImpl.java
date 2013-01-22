package nc.isi.fragaria_adapter_rewrite.services.domain.DsLoader;

import java.util.List;
import java.util.Map;

import nc.isi.fragaria_adapter_rewrite.services.domain.Datasource;

import com.google.common.collect.Maps;

public class MasterDsLoaderImpl implements MasterDsLoader {

	private final Map<String, Datasource> map = Maps.newHashMap();;

	public MasterDsLoaderImpl(List<SpecificDsLoader> list) {
		for (SpecificDsLoader loader : list)
			map.putAll(loader.getDs());
	}

	@Override
	public Map<String, Datasource> getDs() {
		return map;
	}

}
