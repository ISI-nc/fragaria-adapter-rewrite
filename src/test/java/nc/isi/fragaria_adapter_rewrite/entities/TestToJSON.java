package nc.isi.fragaria_adapter_rewrite.entities;

import nc.isi.fragaria_adapter_rewrite.dao.Session;
import nc.isi.fragaria_adapter_rewrite.dao.SessionManager;
import nc.isi.fragaria_adapter_rewrite.model.Directeur;
import nc.isi.fragaria_adapter_rewrite.model.QaRegistry;

import org.apache.tapestry5.ioc.Registry;
import org.junit.Test;

public class TestToJSON {
	private static final Registry registry = QaRegistry.INSTANCE
			.getRegistry();	
	
	@Test
	public void testToJson() {
		Session session;
		SessionManager sessionManager = registry
				.getService(SessionManager.class);
		session = sessionManager.create();
		Directeur directeur = session.create(Directeur.class);
		directeur.setName("Jean-Michel");
		System.out.println(directeur.toJSON());
	}
}
