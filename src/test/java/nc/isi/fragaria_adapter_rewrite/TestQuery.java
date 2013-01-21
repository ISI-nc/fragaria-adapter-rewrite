package nc.isi.fragaria_adapter_rewrite;

import static com.mysema.query.alias.Alias.$;
import static com.mysema.query.alias.Alias.alias;
import static com.mysema.query.collections.MiniApi.from;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;
import nc.isi.fragaria_adapter_rewrite.services.domain.Completion;
import nc.isi.fragaria_adapter_rewrite.services.domain.Entity;
import nc.isi.fragaria_adapter_rewrite.services.domain.EntityMetadata;
import nc.isi.fragaria_adapter_rewrite.services.domain.ByViewQuery;
import nc.isi.fragaria_adapter_rewrite.services.domain.State;
import nc.isi.fragaria_adapter_rewrite.services.domain.ByViewQuery;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class TestQuery extends TestCase {

	private Entity buildEntity() {

		return new Entity() {
			private UUID id;
			private UUID rev;

			@Override
			public void setCompletion(Completion completion) {
				// TODO Auto-generated method stub

			}

			@Override
			public UUID getRev() {
				if (rev == null)
					rev = UUID.randomUUID();
				return rev;
			}

			@Override
			public UUID getId() {
				if (id == null)
					id = UUID.randomUUID();
				return id;
			}

			@Override
			public Completion getCompletion() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void writeProperty(String propertyName, Object value) {
				// TODO Auto-generated method stub

			}

			@Override
			public ObjectNode toJSON() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setState(State state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void registerListener(Object o) {
				// TODO Auto-generated method stub

			}

			@Override
			public <T> T readProperty(Class<T> propertyType, String propertyName) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> Collection<T> readCollection(
					Class<T> collectionGenericType, String collectionName) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<String> getTypes() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public State getState() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EntityMetadata getMetadata() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	private List<Entity> buildEntityCollection() {
		List<Entity> entities = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			entities.add(buildEntity());
		}
		return entities;
	}

	public void testQuery() {
		List<Entity> entities = buildEntityCollection();
		UUID id = entities.get(0).getId();
		ByViewQuery<Entity> query = new ViewQueryImpl<>(Entity.class).where("id", id);
		System.out.println(query.getPredicate());
		System.out.println(id);
		Entity entity = alias(Entity.class);
		Entity entityResult = from($(entity), entities).where(
				query.getPredicate()).uniqueResult($(entity));
		assertTrue(entityResult.getId().equals(id));
	}

}
