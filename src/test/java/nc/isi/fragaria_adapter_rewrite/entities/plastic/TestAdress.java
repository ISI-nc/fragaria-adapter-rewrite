package nc.isi.fragaria_adapter_rewrite.entities.plastic;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TestAdress {
	@Id
	private String id;

	@ManyToOne
	private TestPostalCode testPostalCode;

	private String text;

	protected String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id;
	}

	protected TestPostalCode getTestPostalCode() {
		return testPostalCode;
	}

	protected void setTestPostalCode(TestPostalCode testPostalCode) {
		this.testPostalCode = testPostalCode;
	}

	protected String getText() {
		return text;
	}

	protected void setText(String text) {
		this.text = text;
	}

}
