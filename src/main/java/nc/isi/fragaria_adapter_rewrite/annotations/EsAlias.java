package nc.isi.fragaria_adapter_rewrite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author bjonathas
 * Specifies if an Entity is indexed in an ElasticSearch river 
 * and the name of the alias.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EsAlias {
	String value() default "";
}
