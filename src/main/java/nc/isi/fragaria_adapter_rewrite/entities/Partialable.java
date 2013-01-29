package nc.isi.fragaria_adapter_rewrite.entities;

import nc.isi.fragaria_adapter_rewrite.enums.Completion;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Partialable {

	Completion getCompletion();

	void setCompletion(Completion completion);

	@JsonProperty("_id")
	String getId();

	@JsonProperty("_rev")
	String getRev();

	@JsonProperty("_rev")
	void setRev(String rev);

}
