package nc.isi.fragaria_adapter_rewrite.entities.plastic;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "label", name = "uniqueLabel"))
public class TestSelectable {

	@Id
	private String id;
	private String label;

	protected String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id;
	}

	protected String getLabel() {
		return label;
	}

	protected void setLabel(String label) {
		this.label = label;
	}

}
