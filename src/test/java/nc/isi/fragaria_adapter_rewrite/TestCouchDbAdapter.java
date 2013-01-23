package nc.isi.fragaria_adapter_rewrite;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import junit.framework.TestCase;
import nc.isi.fragaria_adapter_rewrite.services.domain.ByViewQuery;
import nc.isi.fragaria_adapter_rewrite.services.domain.CollectionQueryResponse;
import nc.isi.fragaria_adapter_rewrite.services.domain.CouchDbAdapter;
import nc.isi.fragaria_adapter_rewrite.services.domain.Entity;
import nc.isi.fragaria_adapter_rewrite.services.domain.EntityBuilder;
import nc.isi.fragaria_adapter_rewrite.services.domain.FragariaDomainModule;
import nc.isi.fragaria_adapter_rewrite.services.domain.GenericViews.All;
import nc.isi.fragaria_adapter_rewrite.services.domain.IdQuery;
import nc.isi.fragaria_adapter_rewrite.services.domain.OperationType;
import nc.isi.fragaria_adapter_rewrite.services.domain.Query;
import nc.isi.fragaria_adapter_rewrite.services.domain.session.Session;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

public class TestCouchDbAdapter extends TestCase {
	private static final Registry REGISTRY = RegistryBuilder
			.buildAndStartupRegistry(FragariaDomainModule.class);

	public void testCreate() {
		EntityBuilder entityBuilder = REGISTRY.getService(EntityBuilder.class);
		PersonData personData = entityBuilder.build(PersonData.class);
		Session session = new Session() {

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
			public <T extends Entity> Collection<T> get(Query<T> query) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void delete(Entity... entity) {
				// TODO Auto-generated method stub

			}

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

			public void addChild(Session session) {
				// TODO Auto-generated method stub

			}


			@Override
			public <T extends Entity> void register(OperationType o, T object) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public <T extends Entity> void delete(Collection<T> entity) {
				// TODO Auto-generated method stub
				
			}
		};
		personData.setSession(session);
		personData.setName("Maltat");
		personData.setFirstName("Justin", "Pierre");
		Adress adress = new Adress();
		City paris = entityBuilder.build(City.class);
		paris.setName("Paris");
		paris.setSession(session);
		System.out.println("paris created : " + paris);
		City londres = entityBuilder.build(City.class);
		londres.setName("Londres");
		londres.setSession(session);
		System.out.println("londres created : " + londres);
		CouchDbAdapter couchDbAdapter = REGISTRY
				.getService(CouchDbAdapter.class);
		couchDbAdapter.post(londres, paris);
		System.out.println("londres id : " + londres.getId());
		System.out.println("paris id : " + paris.getId());
		adress.setCity(paris);
		adress.setStreet("Champs Elysée");
		System.out.println("adress paris id : " + adress.getCity().getId());
		personData.setAdress(adress);
		System.out.println("address associated");
		personData.setCity(londres);
		System.out.println("londres associated : "
				+ personData.getCity().getId());
		City[] cities = { londres, paris };
		personData.setCities(Arrays.asList(cities));
		System.out.println("cities associated");
		System.out.println("person : " + personData.toJSON());
		couchDbAdapter.post(londres, paris, personData);
		CollectionQueryResponse<PersonData> personDatas = couchDbAdapter
				.executeQuery(new ByViewQuery<>(PersonData.class, All.class));
		System.out.println(personDatas.getResponse().size());
		for (PersonData temp : personDatas) {
			temp.setSession(session);
			System.out.println(temp.getName());
			System.out.println(temp.getFirstName());
			System.out.println(temp.getAdress());
			if (temp.getAdress() != null)
				System.out.println(temp.getAdress().getCity().getName());
			System.out.println(temp.getCities());
		}
		PersonData fromDB = couchDbAdapter.executeUniqueQuery(
				new IdQuery<>(PersonData.class, personData.getId()))
				.getResponse();
		fromDB.setSession(session);
		for (City city : fromDB.getCities()) {
			System.out.println(city.getRev());
		}
	}
}
