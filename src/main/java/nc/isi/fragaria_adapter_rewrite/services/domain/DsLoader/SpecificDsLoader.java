package nc.isi.fragaria_adapter_rewrite.services.domain.DsLoader;

import java.util.Map;

import nc.isi.fragaria_adapter_rewrite.services.domain.Datasource;

public interface SpecificDsLoader {
	public Map<String, Datasource> getDs();
}
