package com.github.sanctum.labyrinth.annotation;

import com.github.sanctum.labyrinth.data.service.DummyReducer;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * A simple annotation for marking methods/fields or parameters used strictly with json formatting.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_PARAMETER, ElementType.METHOD})
public @interface Json {

	String key() default "";

	Class<? extends Reducer> reducer() default DummyReducer.class;

	interface Reducer {

		Object reduce(Object t);

	}

}
