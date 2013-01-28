package nc.isi.fragaria_adapter_rewrite.annotations;

import nc.isi.fragaria_adapter_rewrite.entities.views.View;

public @interface InView {
	Class<? extends View>[] value();

}
