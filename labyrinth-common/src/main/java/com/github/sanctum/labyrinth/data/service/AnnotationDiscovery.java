package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.data.WideConsumer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * Discover target annotations from either methods or classes.
 *
 * @param <T> A type of annotation.
 * @param <R> A listener to use.
 */
public class AnnotationDiscovery<T extends Annotation, R> implements Iterable<Method>{

	private final int count;
	private final Class<T> annotation;
	private final R r;
	private final Class<R> rClass;
	private final Set<Method> methods = new HashSet<>();

	protected AnnotationDiscovery(Class<T> annotation, R r) {
		this.annotation = annotation;
		this.r = r;
		this.rClass = (Class<R>) r.getClass();
		int annotated = 0;

		for (Method method : rClass.getMethods()) {
			if (method.isAnnotationPresent(annotation)) {
				annotated++;
			}
		}
		this.count = annotated;

	}

	protected AnnotationDiscovery(Class<T> annotation, Class<R> r) {
		this.annotation = annotation;
		this.r = null;
		this.rClass = r;
		int annotated = 0;

		for (Method method : r.getMethods()) {
			if (method.isAnnotationPresent(annotation)) {
				annotated++;
			}
		}
		this.count = annotated;

	}

	public static @NotNull <T extends Annotation, R> AnnotationDiscovery<T, R> of(@NotNull Class<T> c, @NotNull R listener) {
		return new AnnotationDiscovery<>(c, listener);
	}

	public static @NotNull <T extends Annotation, R> AnnotationDiscovery<T, R> of(@NotNull Class<T> c, @NotNull Class<R> listener) {
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
			methods.addAll(Arrays.stream(this.rClass.getMethods()).filter(predicate).collect(Collectors.toList()));
		}
		return this;
	}

	/**
	 * @return true if the desired annotation is present at all.
	 */
	public boolean isPresent() {
		return methods.isEmpty() ? this.rClass.isAnnotationPresent(annotation) : count > 0;
	}

	/**
	 * Run an operation with every annotated method found.
	 *
	 * @param function The function.
	 */
	public void ifPresent(WideConsumer<T, Method> function) {
		if (isPresent()) {
			for (Method m : methods) {
				for (Annotation a : m.getAnnotations()) {
					if (annotation.isAssignableFrom(a.annotationType())) {
						function.accept((T) a, m);
					}
				}
			}
		}
	}

	/**
	 * Get information from the leading source objects located annotation.
	 *
	 * This method gives you access to an annotation and the source object itself.
	 *
	 * @param function The function.
	 * @param <U> The desired return value.
	 * @return A value from an annotation.
	 */
	public <U> U map(AnnotativeConsumer<T, R, U> function) {
		if (isPresent()) {
			for (Annotation a : rClass.getAnnotations()) {
				if (annotation.isAssignableFrom(a.annotationType())) {
					return function.accept((T) a, r);
				}
			}
		}
		return null;
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

	/**
	 * Run an operation for each relative method found.
	 *
	 * @param consumer The method function.
	 */
	@Override
	public void forEach(Consumer<? super Method> consumer) {
		for (Method m : methods) {
			consumer.accept(m);
		}
	}

	@NotNull
	@Override
	public Iterator<Method> iterator() {
		return methods().iterator();
	}

	@Override
	public Spliterator<Method> spliterator() {
		return methods().spliterator();
	}

	@FunctionalInterface
	public interface AnnotativeConsumer<U extends Annotation, R, V> {

		V accept(U r, R u);

	}

}
