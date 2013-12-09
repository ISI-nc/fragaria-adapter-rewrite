package nc.isi.fragaria_adapter_rewrite;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.isi.fragaria_adapter_rewrite.dao.ByViewQuery;
import nc.isi.fragaria_adapter_rewrite.dao.CollectionQueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.Query;
import nc.isi.fragaria_adapter_rewrite.dao.Session;
import nc.isi.fragaria_adapter_rewrite.dao.SessionImpl;
import nc.isi.fragaria_adapter_rewrite.dao.UniqueQueryResponse;
import nc.isi.fragaria_adapter_rewrite.dao.adapters.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.entities.Entity;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;
import nc.isi.fragaria_adapter_rewrite.entities.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.entities.views.ViewConfig;
import nc.isi.fragaria_adapter_rewrite.enums.State;
import nc.isi.fragaria_adapter_rewrite.model.PersonData;
import nc.isi.fragaria_adapter_rewrite.model.QaRegistry;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class TestSession {
	final EntityBuilder entityBuilder = QaRegistry.INSTANCE.getRegistry()
			.getService(EntityBuilder.class);

	private Session session;

	@Before
	public void init() {
		session = buildSession(true);
	}

	@Test
	public void testCreate() {
		Session session = buildSession(false);
		Collection<PersonData> listGet = session.get(new ByViewQuery<>(
				PersonData.class, null));
		int nbBefore = listGet.size();
		PersonData sample = session.create(PersonData.class);
		assertNotNull(sample);
		assertNotNull(sample.getId());
		assertTrue(State.NEW == sample.getState());
		listGet = session.get(new ByViewQuery<>(PersonData.class, null));
		int nbAfter = listGet.size();
		assertTrue(nbAfter == nbBefore + 1);
	}

	@Test
	public void testUpdateAnObjectDoesntChangeListSize() {
		List<PersonData> listGet = new ArrayList<>(
				session.get(new ByViewQuery<>(PersonData.class, null)));
		int sizeBefore = listGet.size();
		PersonData pers = (PersonData) listGet.get(0);
		pers.setName("salut");
		listGet = new ArrayList<>(session.get(new ByViewQuery<>(
				PersonData.class, null)));
		int sizeAfter = listGet.size();
		assertEquals(sizeBefore, sizeAfter);
	}

	@Test
	public void testDelete() {
		List<PersonData> listGet = new ArrayList<>(
				session.get(new ByViewQuery<>(PersonData.class, null)));
		int nbBefore = listGet.size();
		PersonData pers = (PersonData) listGet.get(0);
		session.delete(pers);
		listGet = new ArrayList<>(session.get(new ByViewQuery<>(
				PersonData.class, null)));
		int nbAfter = listGet.size();
		assertTrue(nbAfter == nbBefore - 1);
		assertFalse(listGet.contains(pers));

	}

	@Test
	public void testDeleteCollection() {
		List<PersonData> listGet = new ArrayList<>(
				session.get(new ByViewQuery<>(PersonData.class, null)));
		int nbBefore = listGet.size();
		Collection<PersonData> deletedPersons = Lists.newArrayList();
		for (int i = 0; i < 5; i++) {
			PersonData pers = (PersonData) listGet.get(i);
			session.delete(pers);
			deletedPersons.add(pers);
		}
		listGet = new ArrayList<>(session.get(new ByViewQuery<>(
				PersonData.class, null)));
		int nbAfter = listGet.size();
		assertTrue(nbAfter == nbBefore - deletedPersons.size());
		for (PersonData personData : deletedPersons) {
			assertFalse(listGet.contains(personData));
		}

	}

	private final Session buildSession(boolean addData) {
		final PersonData personData = entityBuilder.build(PersonData.class);
		final List<PersonData> listOfPersons = Lists.newArrayList();
		System.out.println("init");
		SessionImpl session = new SessionImpl(new AdapterManager() {

			@Override
			public void post(List<Entity> entities) {

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

				return new CollectionQueryResponse<T>(new ArrayList<T>());
			}

			@Override
			public Boolean exist(ViewConfig viewConfig,
					EntityMetadata entityMetadata) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void buildView(ViewConfig viewConfig,
					EntityMetadata entityMetadata) {
				// TODO Auto-generated method stub

			}

			@Override
			public Boolean exist(ViewConfig viewConfig,
					Class<? extends Entity> entityClass) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void buildView(ViewConfig viewConfig,
					Class<? extends Entity> entityClass) {
				// TODO Auto-generated method stub

			}
		}, entityBuilder);
		if (addData) {
			for (int i = 0; i < 10; i++) {
				session.create(PersonData.class);
			}
		}

		return (Session) session;
	}
}
