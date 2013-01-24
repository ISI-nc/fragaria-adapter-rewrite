package nc.isi.fragaria_adapter_rewrite.resources;

import java.util.Collection;

public interface ConnectionDataBuilder {

	ConnectionData build(String dsType, Object... params);

	ConnectionData build(String dsType, Collection<Object> params);

}