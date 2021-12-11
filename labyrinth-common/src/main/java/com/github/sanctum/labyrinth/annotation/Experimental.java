package com.github.sanctum.labyrinth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Indicates that a (class, method, parameter or field) is not in stable state yet. It may be renamed, changed or
 * even removed in a future version. This annotation refers to API status only, it doesn't mean that the implementation has
 * any significant bias towards current use.</p>
 *
 * <p>It's safe to use an element marked with this annotation depending on the leading dev comment it holds. However,
 * if the declaration belongs to an external library such usages may lead to problems when the library will be updated to another version.</p>
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
public @interface Experimental {

	/**
	 * @return The developer comment for this experiment.
	 */
	String dueTo() default "no comment";

	/**
	 * @return Whether or not the element this annotation belongs too is set for removal.
	 */
	boolean atRisk() default false;

}
