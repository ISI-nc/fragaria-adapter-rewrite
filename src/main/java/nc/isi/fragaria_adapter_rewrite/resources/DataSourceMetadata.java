package nc.isi.fragaria_adapter_rewrite.resources;

/**
 * Les données permettant de définit une datasource
 * 
 * @author justin
 * 
 */
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

	/**
	 * le type de datasource (clé de recherche)
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Une implémentation de {@link ConnectionData} spécifique au type de
	 * datasource et à la datasource
	 * 
	 * @see ConnectionDataBuilder
	 * @return
	 */
	public ConnectionData getConnectionData() {
		return connectionData;
	}

	/**
	 * Permet de savoir si la datasource accepte les données embeded ou non
	 * 
	 * @return
	 */
	public boolean canEmbed() {
		return canEmbed;
	}
}
