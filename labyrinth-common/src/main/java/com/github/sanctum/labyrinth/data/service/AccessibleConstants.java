package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.library.Deployable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * <h3>This class explicitly implies accessibility.</h3>
 *
 *
 * @param <T>
 */
public abstract class AccessibleConstants<T> implements Iterable<Constant<T>>{

	public abstract Deployable<Field[]> debug();

	public abstract Deployable<Map<Class<?>, List<Object>>> resolve();

	public abstract <O> Deployable<List<O>> resolve(Class<O> c);

	public abstract Constant<?> get(String name);

	public abstract int count();

	public static AccessibleConstants<Object> collect(final @NotNull Type type) {
		return new ParsableAccessibleConstants(type);
	}

	public static AccessibleConstants<Object> collect(final @NotNull Object o) {
		return new ParsableAccessibleConstants(o.getClass());
	}

	public static <T> AccessibleConstants<T> of(final @NotNull Class<T> type) {
		return new UtilityAccessibleConstants<>(type);
	}

	public static <T> AccessibleConstants<T> of(final @NotNull T t) {
		return new UtilityAccessibleConstants<>((Class<T>) t.getClass());
	}

}
