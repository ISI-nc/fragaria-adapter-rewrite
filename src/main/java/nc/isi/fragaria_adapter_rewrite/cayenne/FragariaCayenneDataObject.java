package nc.isi.fragaria_adapter_rewrite.cayenne;

import nc.isi.fragaria_adapter_rewrite.entities.AbstractEntity;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectId;

public class FragariaCayenneDataObject extends CayenneDataObject implements DataObject{
	private final AbstractEntity entity;
	private final  ObjectId objectId;
	
	public FragariaCayenneDataObject(AbstractEntity entity) {
		this.entity = entity;
		this.objectId = new ObjectId(entity.getClass().getSimpleName(), "id", entity.getId());
	}

	@Override
	public Object readProperty(String propName) {
		//TODO
		return null;
	}

}
