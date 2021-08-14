package com.github.sanctum.labyrinth.data.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * Discover methods from objects that are annotated with a specific class.
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

	/**
	 * Filter the methods and only work with ones of interest.
	 *
	 * @param predicate The filtration.
	 * @return The same annotation discovery object.
	 */
	public AnnotationDiscovery<T, R> filter(Predicate<? super Method> predicate) {
		if (methods.isEmpty()) {
			methods.addAll(Arrays.stream(r.getClass().getMethods()).filter(predicate).collect(Collectors.toList()));
		}
		return this;
	}


	/**
	 * Run functions on the given methods.
	 *
	 * @param function The function to run on all methods.
	 */
	public void ifPresent(MethodConsumer<Method, T, R> function) {
		if (methods.isEmpty()) {
			for (Method m : r.getClass().getMethods()) {
				if (m.isAnnotationPresent(annotation)) {
					for (Annotation a : m.getAnnotations()) {
						if (a.getClass().isAssignableFrom(annotation)) {
							function.accept(m, (T) a, r);
						}
					}
				}
			}
		} else {
			for (Method m : methods) {
				if (m.isAnnotationPresent(annotation)) {
					for (Annotation a : m.getAnnotations()) {
						if (a.getClass().isAssignableFrom(annotation)) {
							function.accept(m, (T) a, r);
						}
					}
				}
			}
		}
	}

	/**
	 * @return List's all filtered methods.
	 */
	public Set<Method> methods() {
		return methods;
	}

	/**
	 * Read all annotations from a method that fit this query.
	 *
	 * @param m The method to read.
	 * @return A set of annotations only matching this discovery query.
	 */
	public Set<T> read(Method m) {
		return Arrays.stream(m.getAnnotations()).filter(a -> annotation.isAssignableFrom(a.getClass())).map(a -> (T) a).collect(Collectors.toSet());
	}

	/**
	 * @return The total amount of relevant annotated methods found.
	 */
	public int count() {
		return count;
	}

	@FunctionalInterface
	public interface MethodConsumer<F extends Method, U extends Annotation, R> {

		void accept(F f, U r, R u);

	}

}
