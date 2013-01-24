package nc.isi.fragaria_adapter_rewrite.resources.yaml;

import java.util.Map;

public class YamlDatasourceMetadata {

	private String type;
	private Map<String, Object> connectionData;
	private boolean canEmbed;

	public YamlDatasourceMetadata() {
		this.type = null;
		this.connectionData = null;
		this.canEmbed = false;
	}

	public String getType() {
		return type;
	}

	public Map<String, Object> getConnectionData() {
		return connectionData;
	}

	public boolean canEmbed() {
		return canEmbed;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setConnectionData(Map<String, Object> connectionData) {
		this.connectionData = connectionData;
	}

	public void setCanEmbed(boolean canEmbed) {
		this.canEmbed = canEmbed;
	}

}
