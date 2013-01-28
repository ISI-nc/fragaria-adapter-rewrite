package nc.isi.fragaria_adapter_rewrite.entities.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.resources.ConnectionData;
import nc.isi.fragaria_adapter_rewrite.resources.DataSourceMetadata;
import nc.isi.fragaria_adapter_rewrite.resources.Datasource;
import nc.isi.fragaria_adapter_rewrite.resources.DatasourceImpl;
import nc.isi.fragaria_adapter_rewrite.services.FragariaDomainModule;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;

@SubModule(FragariaDomainModule.class)
public class ViewInitializerTestModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(ViewGeneratorMock.class);
	}

	public void contributeViewInitializer(Configuration<String> configuration) {
		configuration.add("nc.isi.fragaria_adapter_rewrite");
	}

	public void contributeDataSourceProvider(
			MappedConfiguration<String, Datasource> configuration) {
		configuration.add("test", new DatasourceImpl("test",
				new DataSourceMetadata("test", new ConnectionData() {
				}, true)));
	}

	public void contributeViewGeneratorManager(
			MappedConfiguration<String, ViewGenerator> configuration,
			ViewGeneratorMock viewGeneratorMock) {
		configuration.add("test", viewGeneratorMock);
	}

	public void contributeResourceFinder(Configuration<String> configuration) {
		configuration.add("nc.isi.fragaria_adapter_rewrite");
	}

	public void contributeViewConfigProvider(Configuration<String> configuration) {
		configuration.add(".test");
	}

	public void contributeViewConfigBuilderProvider(
			MappedConfiguration<String, ViewConfigBuilder> configuration) {
		configuration.add("test", new ViewConfigBuilder() {

			@Override
			public ViewConfig buildDefault(Class<? extends Entity> entityClass,
					Class<? extends QueryView> view) {
				return new ViewConfigMock("default");
			}

			@Override
			public ViewConfig build(File file) {
				String s;
				try {
					BufferedReader bufferedReader = Files.newBufferedReader(
							Paths.get(file.toURI()), Charset.defaultCharset());
					s = bufferedReader.readLine();
					bufferedReader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return new ViewConfigMock(s);
			}
		});
	}
}
