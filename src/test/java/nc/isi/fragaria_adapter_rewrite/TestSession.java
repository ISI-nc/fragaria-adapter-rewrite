package nc.isi.fragaria_adapter_rewrite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import nc.isi.fragaria_adapter_rewrite.dao.ByViewQuery;
import nc.isi.fragaria_adapter_rewrite.dao.CollectionQueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.Query;
import nc.isi.fragaria_adapter_rewrite.dao.Session;
import nc.isi.fragaria_adapter_rewrite.dao.SessionImpl;
import nc.isi.fragaria_adapter_rewrite.dao.UniqueQueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.adapters.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;
import nc.isi.fragaria_adapter_rewrite.model.PersonData;
import nc.isi.fragaria_adapter_rewrite.services.FragariaDomainModule;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import com.beust.jcommander.internal.Lists;

public class TestSession extends TestCase {
	private static final Registry REGISTRY = RegistryBuilder
			.buildAndStartupRegistry(FragariaDomainModule.class);
	final EntityBuilder entityBuilder = REGISTRY
			.getService(EntityBuilder.class);
	final PersonData personData = entityBuilder.build(PersonData.class);
	final List<PersonData> listOfPersons = Lists.newArrayList();

	public void testCreate() {
		List<String> ids = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			PersonData person = entityBuilder.build(PersonData.class);
			ids.add(person.getId());
			listOfPersons.add(person);
		}

		Session session = buildSession();
		List<PersonData> listGet = new ArrayList<>(
				session.get(new ByViewQuery<>(PersonData.class, null)));
		int nbBefore = listGet.size();
		session.create(PersonData.class);
		listGet = new ArrayList<>(session.get(new ByViewQuery<>(
				PersonData.class, null)));
		int nbAfter = listGet.size();
		assertTrue(nbAfter == nbBefore + 1);
	}

	public void testUpdate() {
		List<String> ids = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			PersonData person = entityBuilder.build(PersonData.class);
			ids.add(person.getId());
			listOfPersons.add(person);
		}

		Session session = buildSession();
		List<PersonData> listGet = new ArrayList<>(
				session.get(new ByViewQuery<>(PersonData.class, null)));
		PersonData pers = (PersonData) listGet.get(0);
		pers.setName("salut");
		listGet = new ArrayList<>(session.get(new ByViewQuery<>(
				PersonData.class, null)));
		assertTrue(listGet.size() == 10);
		listOfPersons.clear();
		listGet = new ArrayList<>(session.get(new ByViewQuery<>(
				PersonData.class, null)));
		assertTrue(listGet.size() == 1);
	}

	public void testUpdateCollection() {
		List<String> ids = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			PersonData person = entityBuilder.build(PersonData.class);
			ids.add(person.getId());
			listOfPersons.add(person);
		}

		Session session = buildSession();
		List<PersonData> listGet = new ArrayList<>(
				session.get(new ByViewQuery<>(PersonData.class, null)));
		for (int i = 0; i < 5; i++) {
			PersonData pers = (PersonData) listGet.get(i);
			pers.setName("salut");
		}
		listGet = new ArrayList<>(session.get(new ByViewQuery<>(
				PersonData.class, null)));
		assertTrue(listGet.size() == 10);
		listOfPersons.clear();
		listGet = new ArrayList<>(session.get(new ByViewQuery<>(
				PersonData.class, null)));
		assertTrue(listGet.size() == 5);
	}

	public void testDelete() {
		List<String> ids = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			PersonData person = entityBuilder.build(PersonData.class);
			ids.add(person.getId());
			listOfPersons.add(person);
		}
		Session session = buildSession();
		List<PersonData> listGet = new ArrayList<>(
				session.get(new ByViewQuery<>(PersonData.class, null)));
		int nbBefore = listGet.size();
		PersonData pers = (PersonData) listGet.get(0);
		session.delete(pers);
		listGet = new ArrayList<>(session.get(new ByViewQuery<>(
				PersonData.class, null)));
		int nbAfter = listGet.size();
		assertTrue(nbAfter == nbBefore - 1);

	}

	public void testDeleteCollection() {
		List<String> ids = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			PersonData person = entityBuilder.build(PersonData.class);
			ids.add(person.getId());
			listOfPersons.add(person);
		}
		Session session = buildSession();
		List<PersonData> listGet = new ArrayList<>(
				session.get(new ByViewQuery<>(PersonData.class, null)));
		int nbBefore = listGet.size();
		for (int i = 0; i < 5; i++) {
			PersonData pers = (PersonData) listGet.get(i);
			session.delete(pers);
		}
		listGet = new ArrayList<>(session.get(new ByViewQuery<>(
				PersonData.class, null)));
		int nbAfter = listGet.size();
		assertTrue(nbAfter == nbBefore - 5);

	}

	public Session buildSession() {

		SessionImpl session = new SessionImpl(new AdapterManager() {

			@Override
			public void post(LinkedList<Entity> entities) {

			}

			@Override
			public void post(Entity... entities) {
				// TODO Auto-generated method stub

			}

			@Override
			public <T extends Entity> UniqueQueryResponse<T> executeUniqueQuery(
					Query<T> query) {
				System.out.println(personData);
				return new UniqueQueryResponse<T>((T) personData);
			}

			@Override
			public <T extends Entity> CollectionQueryResponse<T> executeQuery(
					Query<T> query) {

				return new CollectionQueryResponse<T>(
						(Collection<T>) listOfPersons);
			}
		}, entityBuilder);

		return (Session) session;
	}
}
