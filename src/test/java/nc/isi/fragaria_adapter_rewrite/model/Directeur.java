package nc.isi.fragaria_adapter_rewrite.model;

import java.util.Collection;

import nc.isi.fragaria_adapter_rewrite.annotations.BackReference;
import nc.isi.fragaria_adapter_rewrite.annotations.DsKey;
import nc.isi.fragaria_adapter_rewrite.entities.AbstractEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

@DsKey("loc")
public class Directeur extends AbstractEntity{
	public static final String NAME = "name";
	public static final String ETABLISSEMENTS = "etablissements";

	public Directeur(ObjectNode objectNode) {
		super(objectNode);
	}

	public Directeur() {
		super();
	}

	public String getName() {
		return readProperty(String.class, NAME);
	}

	public void setName(String name) {
		writeProperty(NAME, name);
	}
		
	@BackReference("directeur")
	public Collection<Etablissement> getEtablissements() {
		return readCollection(Etablissement.class, ETABLISSEMENTS);
	}

	public void setEtablissements(Etablissement...etablissement) {
		writeProperty(ETABLISSEMENTS, etablissement);
	}
	
	public void setEtablissements(Collection<Etablissement> etablissements) {
		writeProperty(ETABLISSEMENTS, etablissements);
	}

	@Override
	public String toString() {
		return toJSON().toString();
	}
}
