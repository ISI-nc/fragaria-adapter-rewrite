package nc.isi.fragaria_adapter_rewrite.model;

import nc.isi.fragaria_adapter_rewrite.annotations.BackReference;
import nc.isi.fragaria_adapter_rewrite.annotations.DsKey;
import nc.isi.fragaria_adapter_rewrite.entities.AbstractEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

@DsKey("loc")
public class Etablissement extends AbstractEntity {
	public static final String NAME = "name";
	public static final String DIRECTEUR = "directeur";
	
	public Etablissement(ObjectNode objectNode) {
		super(objectNode);
	}

	public Etablissement() {
		super();
	}

	public String getName() {
		return readProperty(String.class, NAME);
	}

	public void setName(String name) {
		writeProperty(NAME, name);
	}
	
	@BackReference("etablissements")
	public Directeur getDirecteur() {
		return readProperty(Directeur.class, DIRECTEUR);
	}

	public void setDirecteur(Directeur directeur) {
		writeProperty(DIRECTEUR, directeur);
	}

	@Override
	public String toString() {
		return toJSON().toString();
	}
}
