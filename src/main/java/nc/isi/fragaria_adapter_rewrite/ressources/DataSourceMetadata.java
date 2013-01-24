package nc.isi.fragaria_adapter_rewrite.ressources;

public class DataSourceMetadata {
	private final String type;
	private final ConnectionData connectionData;
	private final boolean canEmbed;

	public DataSourceMetadata(String type, ConnectionData connectionData,
			boolean canEmbed) {
		this.type = type;
		this.connectionData = connectionData;
		this.canEmbed = canEmbed;
	}

	public String getType() {
		return type;
	}

	public ConnectionData getConnectionData() {
		return connectionData;
	}

	public boolean isCanEmbed() {
		return canEmbed;
	}
}
