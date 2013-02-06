package nc.isi.fragaria_adapter_rewrite;

import junit.framework.TestCase;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.entities.views.QueryView;
import nc.isi.fragaria_adapter_rewrite.model.CityViews.Name;
import nc.isi.fragaria_adapter_rewrite.model.PersonData;

public class TestEntityMetadata extends TestCase {

	public void testMetadata() throws ClassNotFoundException {
		EntityMetadata entityMetadata = new EntityMetadata(PersonData.class);
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

}
