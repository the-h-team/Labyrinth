package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.data.WideConsumer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Discover target annotations from either methods or classes.
 *
 * @param <T> A type of annotation.
 * @param <R> A listener to use.
 */
public final class AnnotationDiscovery<T extends Annotation, R> implements Iterable<Method>{

	private final int count;
	private final Class<T> annotation;
	private final R r;
	private final Class<R> rClass;
	private Set<Method> methods = new HashSet<>();

	AnnotationDiscovery(Class<T> annotation, R r) {
		this.annotation = annotation;
		this.r = r;
		this.rClass = (Class<R>) r.getClass();
		int annotated = 0;

		for (Method method : rClass.getDeclaredMethods()) {
			try {
				method.setAccessible(true);
			} catch (Exception ignored) {}
			if (method.isAnnotationPresent(annotation)) {
				annotated++;
			}
		}
		this.count = annotated;

	}

	AnnotationDiscovery(Class<T> annotation, Class<R> r) {
		this.annotation = annotation;
		this.r = null;
		this.rClass = r;
		int annotated = 0;

		for (Method method : rClass.getDeclaredMethods()) {
			try {
				method.setAccessible(true);
			} catch (Exception ignored) {}
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

	public AnnotationDiscovery<T, R> sort(Comparator<? super Method> comparator) {
		this.methods = methods.stream().sorted(comparator).collect(Collectors.toCollection(LinkedHashSet::new));
		return this;
	}

	/**
	 * Filter the methods and only work with ones of interest.
	 *
	 * @param hard whether or not to breach accessibility.
	 * @return The same annotation discovery object.
	 */
	public AnnotationDiscovery<T, R> filter(boolean hard) {
		return filter(method -> true, hard);
	}

	/**
	 * Filter the methods and only work with ones of interest.
	 *
	 * @param predicate The filtration.
	 * @return The same annotation discovery object.
	 */
	public AnnotationDiscovery<T, R> filter(Predicate<? super Method> predicate) {
		return filter(predicate, false);
	}

	/**
	 * Filter the methods and only work with ones of interest.
	 *
	 * @param predicate The filtration.
	 * @param hard whether or not to breach accessibility.
	 * @return The same annotation discovery object.
	 */
	public AnnotationDiscovery<T, R> filter(Predicate<? super Method> predicate, boolean hard) {
		if (!hard) {
			if (methods.isEmpty()) {
				methods.addAll(Arrays.stream(this.rClass.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(annotation) && predicate.test(m)).collect(Collectors.toList()));
			}
		} else {
			if (methods.isEmpty()) {
				methods.addAll(Arrays.stream(this.rClass.getDeclaredMethods()).filter(m -> {
					try {
						m.setAccessible(true);
					} catch (Exception ignored){}
					return m.isAnnotationPresent(annotation) && predicate.test(m);
				}).collect(Collectors.toList()));
			}
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
	 * @deprecated Use {@link AnnotationDiscovery#mapFromClass(AnnotativeConsumer)} instead!
	 * @param function The function.
	 * @param <U> The desired return value.
	 * @return A value from an annotation.
	 */
	@Deprecated
	public <U> U map(AnnotativeConsumer<T, R, U> function) {
		return mapFromClass(function);
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
	public <U> U mapFromClass(AnnotativeConsumer<T, R, U> function) {
		if (isPresent()) {
			return function.accept(rClass.getAnnotation(annotation), r);
		}
		return null;
	}

	/**
	 * Get information from the leading source objects methods found with the specified annotation.
	 *
	 * This method gives you access to an annotation and the source object itself.
	 *
	 * @param function The function.
	 * @param <U> The desired return value.
	 * @return A value from an annotation.
	 */
	public <U> List<U> mapFromMethods(AnnotativeConsumer<T, R, U> function) {
		List<U> list = new ArrayList<>();
		ifPresent((t, method) -> list.add(function.accept(t, r)));
		return list;
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
		if (methods.isEmpty()) {
			filter(method -> true).methods.forEach(consumer);
		} else {
			methods.forEach(consumer);
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

		V accept(U annotation, R source);

	}

}
