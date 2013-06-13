package nc.isi.fragaria_adapter_rewrite.entities.views;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.List;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_reflection.services.ResourceFinder;

import com.google.common.collect.Lists;

/**
 * 
 * @author bjonathas
 * 
 *         This service will provide a ViewConfig for a given Class and View
 *         thanks to a ViewConfigBuilder. The ViewConfigBuilder can be different
 *         depending on the Datasource type (like Cayenne or Couchdb). In order
 *         to build the ViewConfig, the ViewConfigBuilder uses the View
 *         definition file which has to : - have the same extension as the one
 *         in fileExtensions for the Datasource Type of this class, - respect
 *         the naming convention, by default : “class-view.ext” (in lower case)
 *         (ex : etablissement-name.sql)
 */

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
		List<String> files = Lists.newArrayList();
		for (String fileExtension : fileExtensions) {
			files.addAll(resourceFinder.getResourcesMatching(String.format(
					FORMAT, entityClass.getSimpleName().toLowerCase(), view
							.getSimpleName().toLowerCase(), fileExtension)));
		}
		checkState(
				files.size() <= 1,
				"trop de fichiers de conf trouver pour la vue %s et l'entité %s",
				view, entityClass);
		return files.size() == 1 ? viewConfigBuilder.build(view.getSimpleName()
				.toLowerCase(), files.get(0)) : viewConfigBuilder.buildDefault(
				entityClass, view);
	}

}
