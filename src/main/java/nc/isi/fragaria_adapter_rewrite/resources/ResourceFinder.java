package nc.isi.fragaria_adapter_rewrite.resources;

import java.io.File;
import java.util.Set;

public interface ResourceFinder {
	Set<File> getResourcesMatching(String regExp);
}
