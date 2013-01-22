package nc.isi.fragaria_adapter_rewrite.services.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Partialable {

	public Completion getCompletion();

	public void setCompletion(Completion completion);

	@JsonProperty("_id")
	public String getId();

	@JsonProperty("_rev")
	public String getRev();

	@JsonProperty("_id")
	public void setId(String id);

	@JsonProperty("_rev")
	public void setRev(String rev);

}
