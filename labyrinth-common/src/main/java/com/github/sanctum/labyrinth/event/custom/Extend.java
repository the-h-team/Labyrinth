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
	 * Sets the key for this annotation.
	 * The key will be used to pass return values from methods with this key in their result processor list.
	 * Works for other {@link Extend} annotated methods and for {@link Subscribe}
	 *
	 * @return the key of the marked method
	 */
	String identifier();

	/**
	 * Sets the keys of methods where the results of this method will be passed to.
	 *
	 * @return the keys of the return-result-consuming methods
	 */
	String[] resultProcessors() default {};

}
