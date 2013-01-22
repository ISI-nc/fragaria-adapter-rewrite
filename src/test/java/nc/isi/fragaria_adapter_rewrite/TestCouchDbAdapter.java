package nc.isi.fragaria_adapter_rewrite;

import java.util.Collection;
import java.util.UUID;

import junit.framework.TestCase;
import nc.isi.fragaria_adapter_rewrite.services.domain.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.services.domain.ByViewQuery;
import nc.isi.fragaria_adapter_rewrite.services.domain.CollectionQueryResponse;
import nc.isi.fragaria_adapter_rewrite.services.domain.CouchDbAdapter;
import nc.isi.fragaria_adapter_rewrite.services.domain.Entity;
import nc.isi.fragaria_adapter_rewrite.services.domain.EntityBuilder;
import nc.isi.fragaria_adapter_rewrite.services.domain.FragariaDomainModule;
import nc.isi.fragaria_adapter_rewrite.services.domain.GenericViews.All;
import nc.isi.fragaria_adapter_rewrite.services.domain.OperationType;
import nc.isi.fragaria_adapter_rewrite.services.domain.Query;
import nc.isi.fragaria_adapter_rewrite.services.domain.Session;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

public class TestCouchDbAdapter extends TestCase {
	private static final Registry REGISTRY = RegistryBuilder
			.buildAndStartupRegistry(FragariaDomainModule.class);

	public void testCreate() {
		EntityBuilder entityBuilder = REGISTRY.getService(EntityBuilder.class);
		PersonData personData = entityBuilder.build(PersonData.class);
		personData.setSession(new Session() {

			@Override
			public void register(OperationType o, Object object) {

			}

			@Override
			public Session post() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T extends Entity> T getUnique(Query<T> query) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public UUID getId() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EntityBuilder getEntityBuilder() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public AdapterManager getAdapterManager() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T extends Entity> Collection<T> get(Query<T> query) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void delete(Collection<Entity> entity) {
				// TODO Auto-generated method stub

			}

			@Override
			public void delete(Entity... entity) {
				// TODO Auto-generated method stub

			}

			@Override
			public Session createChild() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T extends Entity> T create(Class<T> entityClass) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Session cancel() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void addChild(Session session) {
				// TODO Auto-generated method stub

			}
		});
		personData.setName("Maltat");
		personData.setFirstName("Justin", "Pierre");
		Adress adress = new Adress();
		City city = entityBuilder.build(City.class);
		city.setName("Paris");
		adress.setCity(city);
		adress.setStreet("Champs Elysée");
		personData.setAdress(adress);
		CouchDbAdapter couchDbAdapter = REGISTRY
				.getService(CouchDbAdapter.class);
		System.out.println(personData.toJSON());
		couchDbAdapter.post(city, personData);
		CollectionQueryResponse<PersonData> personDatas = couchDbAdapter
				.executeQuery(new ByViewQuery<>(PersonData.class, All.class));
		System.out.println(personDatas.getResponse().size());
		for (PersonData temp : personDatas) {
			System.out.println(temp.getName());
			System.out.println(temp.getFirstName());
			System.out.println(temp.getAdress());
			if (temp.getAdress() != null)
				System.out.println(temp.getAdress().getCity().getName());
		}
	}
}
