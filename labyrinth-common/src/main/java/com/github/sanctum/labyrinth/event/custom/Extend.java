package com.github.sanctum.labyrinth.event.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark consumer methods for results of event handling.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Extend {

	/**
	 * Sets the key for this annotation to be recognised by other Extend annotations
	 *
	 * @return the key of the marked method
	 */
	String identifier();

	/**
	 * Sets the target methods of this extension. If any of these targets gets called, the result will be passed
	 * to the annotated method.
	 *
	 * @return the keys of the parameter-supplying methods
	 */
	String[] targets();

}
