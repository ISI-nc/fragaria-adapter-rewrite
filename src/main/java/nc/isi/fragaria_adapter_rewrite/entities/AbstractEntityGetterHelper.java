package nc.isi.fragaria_adapter_rewrite.entities;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class AbstractEntityGetterHelper extends AbstractEntity {

	public AbstractEntityGetterHelper() {
		super();
	}

	public AbstractEntityGetterHelper(ObjectNode objectNode) {
		super(objectNode);
	}

	public DateTime readDateTimeProperty(String propertyName) {
		return readProperty(DateTime.class, propertyName);
	}

	public Integer readIntegerProperty(String propertyName) {
		return readProperty(Integer.class, propertyName);
	}

	public Double readDoubleProperty(String propertyName) {
		return readProperty(Double.class, propertyName);
	}

	public BigDecimal readBigDecimalProperty(String propertyName) {
		return readProperty(BigDecimal.class, propertyName);
	}

	public String readStringProperty(String propertyName) {
		return readProperty(String.class, propertyName);
	}

	public Boolean readBooleanProperty(String propertyName) {
		return readProperty(Boolean.class, propertyName);
	}

}