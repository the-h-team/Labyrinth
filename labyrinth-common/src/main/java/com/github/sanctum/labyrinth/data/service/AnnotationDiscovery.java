package com.github.sanctum.labyrinth.data.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 *
 *
 * @param <T> A type of annotation.
 * @param <R> A listener to use.
 */
public class AnnotationDiscovery<T extends Annotation, R> {

	private final int count;
	private final Class<T> annotation;
	private final R r;
	private final Set<Method> methods = new HashSet<>();

	protected AnnotationDiscovery(Class<T> annotation, R r) {
		this.annotation = annotation;
		this.r = r;

		int annotated = 0;

		for (Method method : r.getClass().getMethods()) {
			if (method.isAnnotationPresent(annotation)) {
				annotated++;
			}
		}
		this.count = annotated;

	}

	public static @NotNull <T extends Annotation, R> AnnotationDiscovery<T, R> of(@NotNull Class<T> c, @NotNull R listener) {
		return new AnnotationDiscovery<>(c, listener);
	}

	public AnnotationDiscovery<T, R> filter(Predicate<? super Method> predicate) {
		if (methods.isEmpty()) {
			methods.addAll(Arrays.stream(r.getClass().getMethods()).filter(predicate).collect(Collectors.toList()));
		}
		return this;
	}

	public void ifPresent(MethodConsumer<Method, R> function) {
		if (methods.isEmpty()) {
			for (Method m : r.getClass().getMethods()) {
				if (m.isAnnotationPresent(annotation)) {
					function.accept(m, r);
				}
			}
		} else {
			for (Method m : methods) {
				function.accept(m, r);
			}
		}
	}

	public Set<Method> methods() {
		return methods;
	}

	public <U extends Annotation> Set<U> read(Method m, Function<Method, Set<U>> function) {
		return function.apply(m);
	}

	public int count() {
		return count;
	}

	@FunctionalInterface
	public interface MethodConsumer<F extends Method, R> {

		void accept(F f, R r);

	}

}
