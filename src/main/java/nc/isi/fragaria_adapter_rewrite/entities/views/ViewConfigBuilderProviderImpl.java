package nc.isi.fragaria_adapter_rewrite.entities.views;

import java.util.Map;

public class ViewConfigBuilderProviderImpl implements ViewConfigBuilderProvider {
	private final Map<String, ViewConfigBuilder> builders;

	public ViewConfigBuilderProviderImpl(Map<String, ViewConfigBuilder> builders) {
		this.builders = builders;
	}

	@Override
	public ViewConfigBuilder provide(String dsType) {
		return builders.get(dsType);
	}

}
