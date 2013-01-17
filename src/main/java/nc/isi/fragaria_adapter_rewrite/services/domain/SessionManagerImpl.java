package nc.isi.fragaria_adapter_rewrite.services.domain;


public class SessionManagerImpl implements SessionManager {
	private final AdapterManager adapterManager;
	private final EntityBuilder entityBuilder;
		
	public SessionManagerImpl(AdapterManager adapterManager,EntityBuilder entityBuilder) {
		this.adapterManager = adapterManager;
		this.entityBuilder = entityBuilder;
	}

	@Override
	public Session createSession() {
		return new SessionImpl(adapterManager,entityBuilder);
	}

}
