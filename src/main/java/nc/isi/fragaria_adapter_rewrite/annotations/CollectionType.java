package nc.isi.fragaria_adapter_rewrite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectionType {
	@SuppressWarnings("rawtypes")
	Class<? extends Collection> value() default Collection.class;
}
