package nc.isi.fragaria_adapter_rewrite.model;

import nc.isi.fragaria_adapter_rewrite.services.FragariaDomainModule;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.SubModule;

@SubModule(FragariaDomainModule.class)
public class FragariaAdapterModuleQA {

	public void contributeConnectionDataBuilder(
			MappedConfiguration<String, String> configuration) {
		configuration.add("test", SampleConnectionData.class.getName());
	}

	public void contributeResourceFinder(Configuration<String> configuration) {
		configuration.add("nc.isi.fragaria_adapter_rewrite");
	}

}
