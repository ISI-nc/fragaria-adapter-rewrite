package nc.isi.fragaria_adapter_rewrite.entities.views;

public class ViewConfigMock implements ViewConfig {
	private final String content;

	public ViewConfigMock(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

}
