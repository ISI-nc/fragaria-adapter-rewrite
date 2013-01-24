package nc.isi.fragaria_adapter_rewrite.ressources;

import java.util.LinkedHashMap;

public class YamlDatasourceMetadata {

		private String type;
		private LinkedHashMap<String, Object> connectionData;
		private boolean canEmbed;


		public YamlDatasourceMetadata() {
			this.type = null;
			this.connectionData = null;
			this.canEmbed = false;
		}

		public String getType() {
			return type;
		}

		public LinkedHashMap<String, Object> getConnectionData() {
			return connectionData;
		}

		public boolean canEmbed() {
			return canEmbed;
		}
		

		public void setType(String type) {
			this.type = type;
		}

		public void setConnectionData(LinkedHashMap<String, Object> connectionData) {
			this.connectionData = connectionData;
		}

		public void setCanEmbed(boolean canEmbed) {
			this.canEmbed = canEmbed;
		}

}