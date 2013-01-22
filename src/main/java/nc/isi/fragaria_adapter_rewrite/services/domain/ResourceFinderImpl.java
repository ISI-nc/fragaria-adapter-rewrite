package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.testng.collections.Sets;

import com.google.common.collect.Maps;

public class ResourceFinderImpl implements ResourceFinder {
	Map<String,Reflections> map = Maps.newHashMap();
	
	
	public ResourceFinderImpl(List<String> packageNames) {
		for(String packageName : packageNames){
			map.put(packageName, new Reflections(new ConfigurationBuilder()
          .setUrls(ClasspathHelper.forPackage(packageName))
          .setScanners(new ResourcesScanner())));
		}
	}
	
	@Override
	public Set<File> getResourcesMatching(String regExp) {
		Set<File> resources = Sets.newHashSet();   
		for(Reflections reflections : map.values()){
			 Set<String> resFiles = reflections.getResources(Pattern.compile(regExp));
			   for(String res : resFiles){
					resources.add(FileUtils.toFile(
						            this.getClass().getResource("/"+res)));
			   }
		}
		return resources;
	}

}
