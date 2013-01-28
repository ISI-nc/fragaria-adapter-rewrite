package nc.isi.fragaria_adapter_rewrite.entities.views;

public class ViewConfigMock implements ViewConfig {
	private final String content;
	private final String name;

	public ViewConfigMock(String content, String name) {
		this.content = content;
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String getName() {
		return name;
	}

}
