package nc.isi.fragaria_adapter_rewrite.services.domain;

import java.util.UUID;

public interface Partialable {

	public Completion getCompletion();

	public void setCompletion(Completion completion);

	public UUID getId();

	public UUID getRev();

}
