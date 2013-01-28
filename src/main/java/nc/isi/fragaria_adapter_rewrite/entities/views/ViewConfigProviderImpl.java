package nc.isi.fragaria_adapter_rewrite.entities.views;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.util.Collection;
import java.util.List;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.resources.ResourceFinder;

import com.google.common.collect.Lists;

public class ViewConfigProviderImpl implements ViewConfigProvider {
	private final String FORMAT = "%s-%s%s";
	private final ResourceFinder resourceFinder;
	private final Collection<String> fileExtensions;

	public ViewConfigProviderImpl(ResourceFinder resourceFinder,
			Collection<String> fileExtensions) {
		this.resourceFinder = resourceFinder;
		this.fileExtensions = fileExtensions;
	}

	@Override
	public ViewConfig provide(Class<? extends Entity> entityClass,
			Class<? extends QueryView> view, ViewConfigBuilder viewConfigBuilder) {
		List<File> files = Lists.newArrayList();
		for (String fileExtension : fileExtensions) {
			files.addAll(resourceFinder.getResourcesMatching(String.format(
					FORMAT, entityClass.getSimpleName().toLowerCase(), view
							.getSimpleName().toLowerCase(), fileExtension)));
		}
		checkState(
				files.size() <= 1,
				"trop de fichiers de conf trouver pour la vue %s et l'entité %s",
				view, entityClass);
		return files.size() == 1 ? viewConfigBuilder.build(
				view.getSimpleName(), files.get(0)) : viewConfigBuilder
				.buildDefault(entityClass, view);
	}

}
