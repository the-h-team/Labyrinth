package com.github.sanctum.labyrinth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Where javadoc sometimes fails provide safe and easy usage examples or explanations to most object types for IDE's</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
public @interface Note {

	/**
	 * @return The developer comment for this example.
	 */
	String value() default "no comment";

}
