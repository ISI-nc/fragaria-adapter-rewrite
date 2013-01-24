package nc.isi.fragaria_adapter_rewrite.cayenne;

import nc.isi.fragaria_adapter_rewrite.resources.ConnectionData;

public class CayenneConnectionData implements ConnectionData {
	protected String getDatamapName() {
		return datamapName;
	}

	private final String datamapName;

	public CayenneConnectionData(String datamapName) {
		this.datamapName = datamapName;
	}

}
