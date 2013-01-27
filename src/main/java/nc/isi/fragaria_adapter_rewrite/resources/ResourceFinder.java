package nc.isi.fragaria_adapter_rewrite.resources;

import java.io.File;
import java.util.Set;

public interface ResourceFinder {
	/**
	 * Cherche les fichiers correspondant à @param regExp dans les packages
	 * définis par contributeResourceFinder dans le module Tapestry
	 * 
	 * @return
	 */
	Set<File> getResourcesMatching(String regExp);
}
