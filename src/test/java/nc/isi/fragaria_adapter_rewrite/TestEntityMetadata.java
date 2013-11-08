package nc.isi.fragaria_adapter_rewrite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.entities.views.QueryView;
import nc.isi.fragaria_adapter_rewrite.model.CityViews.Name;
import nc.isi.fragaria_adapter_rewrite.model.PersonData;
import nc.isi.fragaria_adapter_rewrite.model.QaRegistry;
import nc.isi.fragaria_adapter_rewrite.services.EntityMetadataProvider;

import org.joda.time.DateTime;
import org.junit.Test;

public class TestEntityMetadata {
	private final EntityMetadataProvider entityMetadataProvider = QaRegistry.INSTANCE
			.getRegistry().getService(EntityMetadataProvider.class);

	@Test
	public void testMetadata() throws ClassNotFoundException {
		EntityMetadata entityMetadata = new EntityMetadata(PersonData.class);
		printMetadata(entityMetadata);
	}

	protected void printMetadata(EntityMetadata entityMetadata) {
		for (String name : entityMetadata.propertyNames()) {
			String message = name + " : " + entityMetadata.propertyType(name);
			for (Class<?> typeVariable : entityMetadata
					.propertyParameterClasses(name)) {
				message += " <" + typeVariable + ">";
			}
			System.out.println(message);
		}
		System.out.println(entityMetadata.propertyNames(Name.class));
		System.out.println(entityMetadata.getViews(QueryView.class));
		System.out.println(entityMetadata.writablesPropertyNames());
	}

	@Test
	public void testSpeed() {
		Long begin = DateTime.now().getMillis();
		System.out.println(begin);
		EntityMetadata entityMetadata = new EntityMetadata(PersonData.class);
		System.out.println("Création d'entityMetadata : "
				+ DateTime.now().minus(begin).getMillis());
		begin = DateTime.now().getMillis();
		printMetadata(entityMetadata);
		System.out.println(begin);
		EntityMetadata second = new EntityMetadata(PersonData.class);
		printMetadata(second);
		System.out.println("seconde création d'entityMetadata : "
				+ DateTime.now().minus(begin).getMillis());
		assertNotSame(entityMetadata, second);

		begin = DateTime.now().getMillis();
		System.out.println(begin);
		EntityMetadata provided = entityMetadataProvider
				.provide(PersonData.class);
		printMetadata(provided);
		System.out.println("provide d'entityMetadata : "
				+ DateTime.now().minus(begin).getMillis());
		begin = DateTime.now().getMillis();
		System.out.println(begin);
		EntityMetadata secondProvided = entityMetadataProvider
				.provide(PersonData.class);
		printMetadata(secondProvided);
		System.out.println("seconde provide d'entityMetadata : "
				+ DateTime.now().minus(begin).getMillis());
		assertEquals(provided, secondProvided);
	}

}
