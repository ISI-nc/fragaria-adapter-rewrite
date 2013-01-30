package nc.isi.fragaria_adapter_rewrite;

import java.util.Arrays;

import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.model.QaRegistry;
import nc.isi.fragaria_reflection.services.ReflectionFactory;

import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

public class TestReflectionFactory {
	private static final String[] packageName = { "nc.isi.fragaria_adapter_rewrite" };

	@Test
	public void test() {
		Reflections reflections = QaRegistry.INSTANCE.getRegistry()
				.getService(ReflectionFactory.class)
				.create(Arrays.asList(packageName), new SubTypesScanner());
		System.out.println(reflections.getSubTypesOf(Entity.class));
		Reflections reflectionsOriginal = new Reflections(
				ConfigurationBuilder.build(packageName[0],
						new SubTypesScanner()));
		System.out.println(reflectionsOriginal.getSubTypesOf(Entity.class));
		Reflections reflectionsWithouBuilder = new Reflections(
				ConfigurationBuilder.build(packageName[0]));
		System.out
				.println(reflectionsWithouBuilder.getSubTypesOf(Entity.class));
	}

}
