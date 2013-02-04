package nc.isi.fragaria_adapter_rewrite.entities;

import java.util.Collection;

import com.fasterxml.jackson.databind.Module;

public class FragariaObjectMapperContributor {
	private final Collection<Module> modules;

	public FragariaObjectMapperContributor(Collection<Module> modules) {
		this.modules = modules;
	}

	public void initialiaze() {
		for (Module module : modules) {
			FragariaObjectMapper.INSTANCE.get().registerModule(module);
		}
	}

}
