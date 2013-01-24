package nc.isi.fragaria_adapter_rewrite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Définit le nom de la référence à cet objet dans l'enfant dans le cadre d'une
 * relation ONE_TO_MANY
 * 
 * @author jmaltat
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BackReference {
	final String DEFAULT = "";

	String value() default DEFAULT;
}
