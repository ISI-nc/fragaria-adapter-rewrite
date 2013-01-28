package nc.isi.fragaria_adapter_rewrite.entities.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import nc.isi.fragaria_adapter_rewrite.dao.CollectionQueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.Query;
import nc.isi.fragaria_adapter_rewrite.dao.UniqueQueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.adapters.Adapter;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.resources.ConnectionData;
import nc.isi.fragaria_adapter_rewrite.resources.DataSourceMetadata;
import nc.isi.fragaria_adapter_rewrite.resources.Datasource;
import nc.isi.fragaria_adapter_rewrite.resources.DatasourceImpl;
import nc.isi.fragaria_adapter_rewrite.services.FragariaDomainModule;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.SubModule;

@SubModule(FragariaDomainModule.class)
public class ViewInitializerTestModule {

	public void contributeViewInitializer(Configuration<String> configuration) {
		configuration.add("nc.isi.fragaria_adapter_rewrite");
	}

	public void contributeDataSourceProvider(
			MappedConfiguration<String, Datasource> configuration) {
		configuration.add("test", new DatasourceImpl("test",
				new DataSourceMetadata("test", new ConnectionData() {
				}, true)));
	}

	public void contributeAdapterManager(
			MappedConfiguration<String, Adapter> configuration) {
		configuration.add("test", new Adapter() {

			@Override
			public void post(List<Entity> entities) {
				// TODO Auto-generated method stub

			}

			@Override
			public void post(Entity... entities) {
				// TODO Auto-generated method stub

			}

			@Override
			public Boolean exist(ViewConfig viewConfig,
					EntityMetadata entityMetadata) {
				return false;
			}

			@Override
			public <T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(
					Query<T> query) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T extends Entity> CollectionQueryResponse<T> executeQuery(
					Query<T> query) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void buildView(ViewConfig viewConfig,
					EntityMetadata entityMetadata) {
				System.out.println(viewConfig.getName());
				if (viewConfig instanceof ViewConfigMock) {
					System.out.println("viewConfig : "
							+ ((ViewConfigMock) viewConfig).getContent());
				} else {
					System.out.println("error");
				}
			}
		});
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
				return new ViewConfigMock(view.getSimpleName(), "default-"
						+ view.getSimpleName().toLowerCase());
			}

			@Override
			public ViewConfig build(String name, File file) {
				String s;
				try {
					BufferedReader bufferedReader = Files.newBufferedReader(
							Paths.get(file.toURI()), Charset.defaultCharset());
					s = bufferedReader.readLine();
					bufferedReader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return new ViewConfigMock(name, s);
			}
		});
	}
}
