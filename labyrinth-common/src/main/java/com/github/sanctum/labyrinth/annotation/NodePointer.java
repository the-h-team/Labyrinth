package com.github.sanctum.labyrinth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

/**
 * This annotation marks the exact package location of a JSON serializable object.
 *
 * @author Hempfest
 * @version 1.0
 * <p> Naming the value under this annotation anything other than the class it represents will result in
 * failure to to read.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NodePointer {

	/**
	 * @return The class pointer for an object.
	 */
	@NotNull String value();

}
