package com.github.sanctum.labyrinth.data.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
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
	 * @return true if the desired annotation is present at all.
	 */
	public boolean isPresent() {
		return methods.isEmpty() ? this.r.getClass().isAnnotationPresent(annotation) : count > 0;
	}

	/**
	 * Run an operation with every annotation found.
	 *
	 * @param function The function.
	 */
	public void ifPresent(AnnotativeConsumer<T, R, Void> function) {
		if (isPresent()) {
			for (Method m : methods) {
				for (Annotation a : m.getAnnotations()) {
					if (annotation.isAssignableFrom(a.annotationType())) {
						function.accept((T) a, r);
					}
				}
			}
		}
	}

	/**
	 * Map a value from an annotation if present.
	 *
	 * This method gives you access to an annotation and the source object itself.
	 *
	 * @param function The function.
	 * @param <U> The desired return value.
	 * @return A value from an annotation.
	 */
	public <U> U map(AnnotativeConsumer<T, R, U> function) {
		if (isPresent()) {
			for (Annotation a : r.getClass().getAnnotations()) {
				if (annotation.isAssignableFrom(a.annotationType())) {
					return function.accept((T) a, r);
				}
			}
		}
		return null;
	}

	/**
	 * Run an operation for each relative method found.
	 *
	 * @param consumer The method function.
	 */
	public void forEach(Consumer<Method> consumer) {
		for (Method m : methods) {
			consumer.accept(m);
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
	public interface AnnotativeConsumer<U extends Annotation, R, V> {

		V accept(U r, R u);

	}

}
