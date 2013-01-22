package nc.isi.fragaria_adapter_rewrite.services.domain.session;

import nc.isi.fragaria_adapter_rewrite.services.domain.AdapterManager;
import nc.isi.fragaria_adapter_rewrite.services.domain.EntityBuilder;

/**
 * 
 * @author bjonathas
 *
 *Classe permettant la création de session.
 */
public class SessionManagerImpl implements SessionManager {
	private final AdapterManager adapterManager;
	private final EntityBuilder entityBuilder;
	private final QueryExecutorForCollection qExecutor;
		
	public SessionManagerImpl(AdapterManager adapterManager,EntityBuilder entityBuilder,QueryExecutorForCollection qExecutor) {
		this.adapterManager = adapterManager;
		this.entityBuilder = entityBuilder;
		this.qExecutor = qExecutor;
	}

	@Override
	public Session createSession() {
		return new SessionImpl(adapterManager,entityBuilder,qExecutor);
	}

}
