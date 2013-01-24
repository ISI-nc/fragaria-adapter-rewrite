package nc.isi.fragaria_adapter_rewrite;

import junit.framework.TestCase;
import nc.isi.fragaria_adapter_rewrite.cayenne.FragariaCayenneDataObject;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;
import nc.isi.fragaria_adapter_rewrite.model.Etablissement;
import nc.isi.fragaria_adapter_rewrite.services.FragariaDomainModule;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

public class TestCayenne extends TestCase{
	private ObjectContext context;
	private static final Registry REGISTRY = RegistryBuilder
			.buildAndStartupRegistry(FragariaDomainModule.class);
	final EntityBuilder entityBuilder = REGISTRY
			.getService(EntityBuilder.class);
	
	 protected void setUp() {
		ServerRuntime cayenneRuntime = new ServerRuntime("cayenne-datamap.xml");
		context = cayenneRuntime.getContext();
     }
	
	
	public void testFragariaCayenneDataObject(){
		Etablissement etablissement = entityBuilder.build(Etablissement.class);
		etablissement.setName("ATIR");
		FragariaCayenneDataObject cayenneEtablissement = new FragariaCayenneDataObject(etablissement);
		context.registerNewObject(cayenneEtablissement);
		context.commitChanges();
	}
}
