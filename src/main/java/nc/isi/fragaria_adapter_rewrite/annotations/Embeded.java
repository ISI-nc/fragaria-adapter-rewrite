package nc.isi.fragaria_adapter_rewrite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nc.isi.fragaria_adapter_rewrite.entities.views.EmbedingView;
import nc.isi.fragaria_adapter_rewrite.entities.views.GenericEmbedingViews.Id;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Embeded {
	Class<? extends EmbedingView> value() default Id.class;
}
