package nc.isi.fragaria_adapter_rewrite.model;

import nc.isi.fragaria_adapter_rewrite.annotations.DsKey;
import nc.isi.fragaria_adapter_rewrite.annotations.InView;
import nc.isi.fragaria_adapter_rewrite.entities.AbstractEntity;
import nc.isi.fragaria_adapter_rewrite.model.CityViews.Name;

@DsKey("test")
public class City extends AbstractEntity {
	public static final String NAME = "name";

	public City() {
		super();
	}

	@InView(Name.class)
	public String getName() {
		return readProperty(String.class, NAME);
	}

	public void setName(String name) {
		writeProperty(NAME, name);
	}

	@Override
	public String toString() {
		return toJSON().toString();
	}

}
