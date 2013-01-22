package nc.isi.fragaria_adapter_rewrite;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import nc.isi.fragaria_adapter_rewrite.services.domain.ConnectionData;
import nc.isi.fragaria_adapter_rewrite.services.domain.CouchdbConnectionData;
import nc.isi.fragaria_adapter_rewrite.services.domain.DataSourceMetadata;
import nc.isi.fragaria_adapter_rewrite.services.domain.Datasource;
import nc.isi.fragaria_adapter_rewrite.services.domain.FragariaDomainModule;
import nc.isi.fragaria_adapter_rewrite.services.domain.DsLoader.YamlDsLoader;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;

public class TestYamlDsLoader extends TestCase {
	private static final Registry REGISTRY = RegistryBuilder
			.buildAndStartupRegistry(FragariaDomainModule.class);

	public void testYamlDsLoader() {
		List<String> pack = Lists.newArrayList();
		pack.add("nc.isi.fragaria_adapter_rewrite");
		Map<String, Datasource> map = Maps.newHashMap();
		Map<String, Class<? extends ConnectionData>> connectionDataMap = Maps
				.newHashMap();
		connectionDataMap.put("COUCHDB", CouchdbConnectionData.class);
		YamlDsLoader loader = REGISTRY.getService(YamlDsLoader.class);
		Datasource dsFragaria = new DatasourceImpl("fragaria",
				new DataSourceMetadata("COUCHDB", new CouchdbConnectionData(
						"http://localhost:5984/", "fragaria"), true));
		Datasource loaded = loader.getDs().get("fragaria");
		CouchdbConnectionData dsFragariaConnectionData = (CouchdbConnectionData) dsFragaria
				.getDsMetadata().getConnectionData();
		CouchdbConnectionData loadedConnectionData = (CouchdbConnectionData) loaded
				.getDsMetadata().getConnectionData();
		assertTrue(loaded.getKey().equals(dsFragaria.getKey()));
		assertTrue(loaded.getDsMetadata().getType()
				.equals(dsFragaria.getDsMetadata().getType()));
		assertTrue(loaded.getDsMetadata().getClass() == dsFragaria
				.getDsMetadata().getClass());
		assertTrue(loadedConnectionData.getClass() == dsFragariaConnectionData
				.getClass());
		assertTrue(loadedConnectionData.getDbName().equals(
				dsFragariaConnectionData.getDbName()));
		assertTrue(loadedConnectionData.getUrl().equals(
				dsFragariaConnectionData.getUrl()));
	}
}
