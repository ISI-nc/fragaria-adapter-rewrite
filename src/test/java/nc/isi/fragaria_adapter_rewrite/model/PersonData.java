package nc.isi.fragaria_adapter_rewrite.model;

import java.util.Arrays;
import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.annotations.DsKey;
import nc.isi.fragaria_adapter_rewrite.annotations.Embeded;
import nc.isi.fragaria_adapter_rewrite.annotations.InView;
import nc.isi.fragaria_adapter_rewrite.entities.AbstractEntity;
import nc.isi.fragaria_adapter_rewrite.model.CityViews.Name;

@DsKey("test")
public class PersonData extends AbstractEntity {

	public static final String NAME = "name";
	public static final String FIRST_NAME = "firstName";
	public static final String ADRESS = "adress";
	public static final String CITIES = "cities";
	public static final String CITY = "city";

	public PersonData() {
		super();
	}

	@InView(Name.class)
	public String getName() {
		return readProperty(String.class, NAME);
	}

	public void setName(String name) {
		writeProperty(NAME, name);
	}

	public Collection<String> getFirstName() {
		return readCollection(String.class, FIRST_NAME);
	}

	public void setFirstName(String... firstNames) {
		writeProperty(FIRST_NAME, Arrays.asList(firstNames));
	}

	public Adress getAdress() {
		return readProperty(Adress.class, ADRESS);
	}

	public void setAdress(Adress adress) {
		writeProperty(ADRESS, adress);
	}

	@Embeded(Name.class)
	public Collection<City> getCities() {
		return readCollection(City.class, CITIES);
	}

	public void setCities(Collection<City> cities) {
		writeProperty(CITIES, cities);
	}

	@Embeded(Name.class)
	public City getCity() {
		return readProperty(City.class, CITY);
	}

	public void setCity(City city) {
		writeProperty(CITY, city);
	}

	@Override
	public String toString() {
		return getId();
	}

}
