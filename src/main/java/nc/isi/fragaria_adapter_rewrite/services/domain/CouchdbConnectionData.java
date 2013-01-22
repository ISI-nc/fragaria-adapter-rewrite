package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.net.MalformedURLException;
import java.net.URL;

public class CouchdbConnectionData implements ConnectionData {
	private final URL url;
	private final String dbName;

	public CouchdbConnectionData(URL url, String dbName) {
		this.url = url;
		this.dbName = dbName;
	}

	public CouchdbConnectionData(String url, String dbName) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		this.dbName = dbName;
	}


	public URL getUrl() {
		return url;
	}

	public String getDbName() {
		return dbName;
	}

}
