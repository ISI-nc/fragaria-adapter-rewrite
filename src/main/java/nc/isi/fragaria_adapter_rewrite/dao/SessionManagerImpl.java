package nc.isi.fragaria_adapter_rewrite.dao;

import nc.isi.fragaria_adapter_rewrite.dao.adapters.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.entities.EntityBuilder;

/**
 * 
 * @author bjonathas
 * 
 *         Classe permettant la création de session.
 */
public class SessionManagerImpl implements SessionManager {
	private final AdapterManager adapterManager;
	private final EntityBuilder entityBuilder;

	public SessionManagerImpl(AdapterManager adapterManager,
			EntityBuilder entityBuilder) {
		this.adapterManager = adapterManager;
		this.entityBuilder = entityBuilder;
	}

	@Override
	public Session create() {
		return new SessionImpl(adapterManager, entityBuilder);
	}

}
