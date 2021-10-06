package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.data.service.DummyAdapter;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation used to mark the exact package location of a JSON serializable object.
 *
 * But now with less restrictions than v1.0 you can serialize objects under any alias.
 *
 * @author Hempfest
 * @version 2.0
 * <p> Naming the value under this annotation anything other than the class it represents will result in
 * failure to to read.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NodePointer {

	/**
	 * @return The alias for this object.
	 */
	String value();

	/**
	 * Optional re-direction.
	 *
	 * @return The implementation for adapting. Comparative to bukkit's {@link org.bukkit.configuration.serialization.DelegateDeserialization}
	 */
	Class<? extends JsonAdapter<?>> type() default DummyAdapter.class;

}
