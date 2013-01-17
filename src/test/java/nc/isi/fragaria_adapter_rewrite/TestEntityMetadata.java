package nc.isi.fragaria_adapter_rewrite;

import java.lang.reflect.Type;

import junit.framework.TestCase;
import nc.isi.fragaria_adapter_rewrite.services.domain.EntityMetadata;

public class TestEntityMetadata extends TestCase {

	public void testMetadata() {
		EntityMetadata entityMetadata = new EntityMetadata(PersonData.class);
		for (String name : entityMetadata.propertyNames()) {
			String message = name + " : " + entityMetadata.propertyType(name);
			for (Type typeVariable : entityMetadata
					.propertyParameterTypes(name)) {
				message += " <" + typeVariable + ">";
			}
			System.out.println(message);
			System.out.println(entityMetadata.getBackReference(name));
		}
	}

}
