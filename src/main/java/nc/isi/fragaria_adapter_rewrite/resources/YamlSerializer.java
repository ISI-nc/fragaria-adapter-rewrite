package nc.isi.fragaria_adapter_rewrite.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

public class YamlSerializer {

	public <T> T serialize(File file, Class<T> clazz)
			throws FileNotFoundException {
		InputStream input = new FileInputStream(file);
		Yaml yaml = new Yaml();
		return yaml.loadAs(input, clazz);
	}
}
