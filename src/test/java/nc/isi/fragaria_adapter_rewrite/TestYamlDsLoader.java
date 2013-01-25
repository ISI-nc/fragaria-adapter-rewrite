package nc.isi.fragaria_adapter_rewrite;

import junit.framework.TestCase;
import nc.isi.fragaria_adapter_rewrite.model.QaRegistry;
import nc.isi.fragaria_adapter_rewrite.model.SampleConnectionData;
import nc.isi.fragaria_adapter_rewrite.resources.DataSourceMetadata;
import nc.isi.fragaria_adapter_rewrite.resources.Datasource;
import nc.isi.fragaria_adapter_rewrite.resources.DatasourceImpl;
import nc.isi.fragaria_adapter_rewrite.resources.yaml.YamlDsLoader;

public class TestYamlDsLoader extends TestCase {

	public void testYamlDsLoader() {
		YamlDsLoader loader = QaRegistry.INSTANCE.getRegistry().getService(
				YamlDsLoader.class);
		Datasource dsFragaria = new DatasourceImpl("rer-test",
				new DataSourceMetadata("test", new SampleConnectionData(
						"http://localhost:5984/", "rer"), true));
		Datasource loaded = loader.getDs().get("rer-test");
		SampleConnectionData dsFragariaConnectionData = (SampleConnectionData) dsFragaria
				.getDsMetadata().getConnectionData();
		SampleConnectionData loadedConnectionData = (SampleConnectionData) loaded
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
