package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.io.File;
import java.util.Set;

public interface ResourceFinder {
	Set<File> getResourcesMatching(String regExp);
}
