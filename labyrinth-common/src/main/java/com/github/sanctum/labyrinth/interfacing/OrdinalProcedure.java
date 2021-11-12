package com.github.sanctum.labyrinth.interfacing;

import com.github.sanctum.labyrinth.annotation.Ordinal;
import com.github.sanctum.labyrinth.data.service.AnnotationDiscovery;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This is an interface/utility used to finalize building procedures or access private methods from objects.
 *
 * If the intended object for use contains no usage of {@link Ordinal} this utility has no use to you.
 *
 */
public abstract class OrdinalProcedure {

	OrdinalProcedure() {}

	/**
	 * Process all flagged ordinal's  within every object.
	 *
	 * @param elements The elements to process.
	 * @param <E> The type of element.
	 * @return A list of processed elements.
	 */
	public static <E> List<OrdinalElement<E>> process(Iterable<E> elements) {
		List<OrdinalElement<E>> processed = new ArrayList<>();
		for (E o : elements) {
			processed.add(process(o));
		}
		return processed;
	}

	/**
	 * Process all flagged ordinal's 
	 *
	 * @param element The element to process.
	 * @param <E> The type of element.
	 * @return A processed element.
	 */
	public static <E> OrdinalElement<E> process(E element) {
		AnnotationDiscovery<Ordinal, E> discovery = AnnotationDiscovery.of(Ordinal.class, element).filter(true).sort(Comparator.comparingInt(value -> value.getAnnotation(Ordinal.class).value()));
		for (Method m : discovery) {
			try {
				m.invoke(element);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ProcessedOrdinalElement<>(element);
	}

	/**
	 * Process a specific ordinal 
	 *
	 * @param element The element to process.
	 * @param ordinal The ordinal to process.
	 * @param <E> The type of element.
	 * @return A processed element.
	 */
	public static <E> OrdinalElement<E> process(E element, int ordinal) {
		AnnotationDiscovery<Ordinal, E> discovery = AnnotationDiscovery.of(Ordinal.class, element).filter(true);
		discovery.ifPresent((ord, method) -> {
			if (ord.value() == ordinal) {
				try {
					method.invoke(element);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return new ProcessedOrdinalElement<>(element);
	}

	/**
	 * Process all ordinals within range (0 - specified) in order.
	 *
	 * @param element The element to process.
	 * @param ordinal The max ordinal range to process.
	 * @param <E> The type of element.
	 * @return A processed element.
	 */
	public static <E> OrdinalElement<E> processMax(E element, int ordinal) {
		AnnotationDiscovery<Ordinal, E> discovery = AnnotationDiscovery.of(Ordinal.class, element).filter(true).sort(Comparator.comparingInt(value -> value.getAnnotation(Ordinal.class).value()));
		discovery.ifPresent((ord, method) -> {
			if (ord.value() <= ordinal) {
				try {
					method.invoke(element);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return new ProcessedOrdinalElement<>(element);
	}

	/**
	 * Process all ordinals within range (specified+) in order, ordinals lower than specified will not process.
	 *
	 * @param element The element to process.
	 * @param ordinal The max ordinal range to process.
	 * @param <E> The type of element.
	 * @return A processed element.
	 */
	public static <E> OrdinalElement<E> processMin(E element, int ordinal) {
		AnnotationDiscovery<Ordinal, E> discovery = AnnotationDiscovery.of(Ordinal.class, element).filter(true).sort(Comparator.comparingInt(value -> value.getAnnotation(Ordinal.class).value()));
		discovery.ifPresent((ord, method) -> {
			if (ord.value() >= ordinal) {
				try {
					method.invoke(element);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return new ProcessedOrdinalElement<>(element);
	}

	/**
	 * Get a specific method from the sourced element using its declared ordinal.
	 *
	 * @param element The element to use.
	 * @param ordinal The ordinal to use.
	 * @param <E> The element source type.
	 * @return A generic ordinal containing synchronized information.
	 */
	public static <E> GenericOrdinalElement select(E element, int ordinal) {
		AnnotationDiscovery<Ordinal, E> discovery = AnnotationDiscovery.of(Ordinal.class, element).filter(true);
		return discovery.methods().stream().filter(m -> m.getAnnotation(Ordinal.class).value() == ordinal).findFirst().map(method -> {
			try {
				return new GenericOrdinalElement(method.invoke(element));
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return null;
		}).orElseThrow(() -> new RuntimeException("Ordinal " + ordinal + " either not found or access failed."));
	}

	/**
	 * Get a specific method from the sourced element using its declared ordinal.
	 *
	 * @param element The element to use.
	 * @param ordinal The ordinal to use.
	 * @param args The object arguments to use if the method so requires.
	 * @param <E> The element source type.
	 * @return A generic ordinal containing synchronized information.
	 */
	public static <E> GenericOrdinalElement select(E element, int ordinal, Object... args) {
		AnnotationDiscovery<Ordinal, E> discovery = AnnotationDiscovery.of(Ordinal.class, element).filter(true);
		return discovery.methods().stream().filter(m -> m.getAnnotation(Ordinal.class).value() == ordinal).findFirst().map(method -> {
			try {
				return new GenericOrdinalElement(method.invoke(element, args));
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return null;
		}).orElseThrow(() -> new RuntimeException("Ordinal " + ordinal + " either not found or access failed."));
	}

}