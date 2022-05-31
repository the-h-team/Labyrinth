package com.github.sanctum.labyrinth.interfacing;

import com.github.sanctum.labyrinth.annotation.Ordinal;
import com.github.sanctum.labyrinth.data.OrdinalProcessException;
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
public abstract class OrdinalProcedure<E> {

	private E e;
	private Iterable<E> eI;

	protected OrdinalProcedure(E e) {
		this.e = e;
	}

	protected OrdinalProcedure(Iterable<E> e) {
		this.eI = e;
	}

	/**
	 * Process all flagged ordinal's  within every object.
	 *
	 * @return A list of processed elements.
	 */
	public OrdinalResult<E> run() {
		if (isIterable()) {
			List<OrdinalElement<E>> processed = new ArrayList<>();
			for (E o : eI) {
				processed.add(run(o));
			}
			return OrdinalResult.of(processed);
		} else {
			return OrdinalResult.of(run(e));
		}
	}

	/**
	 * Process a specific ordinal
	 *
	 * @param ordinal The ordinal to process.
	 * @return A processed element.
	 */
	public OrdinalResult<E> run(int ordinal) {
		if (isIterable()) {
			List<OrdinalElement<E>> processed = new ArrayList<>();
			for (E o : eI) {
				processed.add(run(o, ordinal));
			}
			return OrdinalResult.of(processed);
		} else {
			return OrdinalResult.of(run(e, ordinal));
		}
	}

	/**
	 * Process all ordinals within range (0 - specified) in order.
	 *
	 * @param ordinal The max ordinal range to process.
	 * @return A processed element.
	 */
	public OrdinalResult<E> max(int ordinal) {
		if (isIterable()) {
			List<OrdinalElement<E>> processed = new ArrayList<>();
			for (E o : eI) {
				processed.add(max(o, ordinal));
			}
			return OrdinalResult.of(processed);
		} else {
			return OrdinalResult.of(max(e, ordinal));
		}
	}

	/**
	 * Process all ordinals within range (specified+) in order, ordinals lower than specified will not process.
	 *
	 * @param ordinal The max ordinal range to process.
	 * @return A processed element.
	 */
	public OrdinalResult<E> min(int ordinal) {
		if (isIterable()) {
			List<OrdinalElement<E>> processed = new ArrayList<>();
			for (E o : eI) {
				processed.add(min(o, ordinal));
			}
			return OrdinalResult.of(processed);
		} else {
			return OrdinalResult.of(min(e, ordinal));
		}
	}

	/**
	 * Get a specific method from the sourced element using its declared ordinal.
	 *
	 * @param ordinal The ordinal to use.
	 * @return A generic ordinal containing synchronized information.
	 */
	public OrdinalResult<Object> get(int ordinal) {
		if (isIterable()) {
			List<GenericOrdinalElement> processed = new ArrayList<>();
			for (E o : eI) {
				processed.add(get(o, ordinal));
			}
			return OrdinalResult.of(processed);
		} else {
			return OrdinalResult.of(get(e, ordinal));
		}
	}

	/**
	 * Get a specific method from the sourced element using its declared ordinal.
	 *
	 * @param ordinal The ordinal to use.
	 * @param args The object arguments to use if the method so requires.
	 * @return A generic ordinal containing synchronized information.
	 */
	public OrdinalResult<Object> get(int ordinal, Object... args) {
		if (isIterable()) {
			List<GenericOrdinalElement> processed = new ArrayList<>();
			for (E o : eI) {
				processed.add(get(o, ordinal, args));
			}
			return OrdinalResult.of(processed);
		} else {
			return OrdinalResult.of(get(e, ordinal, args));
		}
	}

	public boolean isIterable() {
		return eI != null;
	}

	private OrdinalElement<E> run(E element) {
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

	private OrdinalElement<E> run(E element, int ordinal) {
		AnnotationDiscovery<Ordinal, E> discovery = AnnotationDiscovery.of(Ordinal.class, element).filter(true);
		discovery.ifPresent((ord, method) -> {
			if (ord.value() == ordinal) {
				try {
					method.invoke(element);
				} catch (Exception e) {
					if (e.getCause() != null) {
						if (e.getCause().getMessage() == null) {
							throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getTypeName(), e.getCause().getStackTrace());
						} else {
							throw new OrdinalProcessException("Ordinal failure (#" + ordinal + ") @ " + element.getClass().getTypeName() + " : " + '"' + e.getCause().getMessage() + '"', e.getCause().getStackTrace());
						}
					} else {
						throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getSimpleName());
					}
				}
			}
		});
		return new ProcessedOrdinalElement<>(element);
	}

	private OrdinalElement<E> max(E element, int ordinal) {
		AnnotationDiscovery<Ordinal, E> discovery = AnnotationDiscovery.of(Ordinal.class, element).filter(true).sort(Comparator.comparingInt(value -> value.getAnnotation(Ordinal.class).value()));
		discovery.ifPresent((ord, method) -> {
			if (ord.value() <= ordinal) {
				try {
					method.invoke(element);
				} catch (Exception e) {
					if (e.getCause() != null) {
						if (e.getCause().getMessage() == null) {
							throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getTypeName(), e.getCause().getStackTrace());
						} else {
							throw new OrdinalProcessException("Ordinal failure (#" + ordinal + ") @ " + element.getClass().getTypeName() + " : " + '"' + e.getCause().getMessage() + '"', e.getCause().getStackTrace());
						}
					} else {
						throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getSimpleName());
					}
				}
			}
		});
		return new ProcessedOrdinalElement<>(element);
	}

	private OrdinalElement<E> min(E element, int ordinal) {
		AnnotationDiscovery<Ordinal, E> discovery = AnnotationDiscovery.of(Ordinal.class, element).filter(true).sort(Comparator.comparingInt(value -> value.getAnnotation(Ordinal.class).value()));
		discovery.ifPresent((ord, method) -> {
			if (ord.value() >= ordinal) {
				try {
					method.invoke(element);
				} catch (Exception e) {
					if (e.getCause() != null) {
						if (e.getCause().getMessage() == null) {
							throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getTypeName(), e.getCause().getStackTrace());
						} else {
							throw new OrdinalProcessException("Ordinal failure (#" + ordinal + ") @ " + element.getClass().getTypeName() + " : " + '"' + e.getCause().getMessage() + '"', e.getCause().getStackTrace());
						}
					} else {
						throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getSimpleName());
					}
				}
			}
		});
		return new ProcessedOrdinalElement<>(element);
	}

	private GenericOrdinalElement get(E element, int ordinal) {
		AnnotationDiscovery<Ordinal, E> discovery = AnnotationDiscovery.of(Ordinal.class, element).filter(true);
		return discovery.methods().stream().filter(m -> m.getAnnotation(Ordinal.class).value() == ordinal).findFirst().map(method -> {
			try {
				return new GenericOrdinalElement(method.invoke(element));
			} catch (Exception exception) {
				if (exception.getCause() != null) {
					if (exception.getCause().getMessage() == null) {
						throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getTypeName(), exception.getCause().getStackTrace());
					} else {
						throw new OrdinalProcessException("Ordinal failure (#" + ordinal + ") @ " + element.getClass().getTypeName() + " : " + '"' + exception.getCause().getMessage() + '"', exception.getCause().getStackTrace());
					}
				} else {
					throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getSimpleName());
				}
			}
		}).orElseThrow(() -> new RuntimeException("Ordinal " + ordinal + " either not found or access failed."));
	}

	private GenericOrdinalElement get(E element, int ordinal, Object... args) {
		AnnotationDiscovery<Ordinal, E> discovery = AnnotationDiscovery.of(Ordinal.class, element).filter(true);
		return discovery.methods().stream().filter(m -> m.getAnnotation(Ordinal.class).value() == ordinal).findFirst().map(method -> {
			try {
				return new GenericOrdinalElement(method.invoke(element, args));
			} catch (Exception exception) {
				if (exception.getCause() != null) {
					if (exception.getCause().getMessage() == null) {
						throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getTypeName(), exception.getCause().getStackTrace());
					} else {
						throw new OrdinalProcessException("Ordinal failure (#" + ordinal + ") @ " + element.getClass().getTypeName() + " : " + '"' + exception.getCause().getMessage() + '"', exception.getCause().getStackTrace());
					}
				} else {
					throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getSimpleName());
				}
			}
		}).orElseThrow(() -> new RuntimeException("Ordinal " + ordinal + " either not found or access failed."));
	}

	public static <E> OrdinalProcedure<E> of(E e) {
		return new OrdinalProcedure<E>(e) {};
	}

	public static <E> OrdinalProcedure<E> of(Iterable<E> e) {
		return new OrdinalProcedure<E>(e) {};
	}

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
					if (e.getCause() != null) {
						if (e.getCause().getMessage() == null) {
							throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getTypeName(), e.getCause().getStackTrace());
						} else {
							throw new OrdinalProcessException("Ordinal failure (#" + ordinal + ") @ " + element.getClass().getTypeName() + " : " + '"' + e.getCause().getMessage() + '"', e.getCause().getStackTrace());
						}
					} else {
						throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getSimpleName());
					}
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
					if (e.getCause() != null) {
						if (e.getCause().getMessage() == null) {
							throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getTypeName(), e.getCause().getStackTrace());
						} else {
							throw new OrdinalProcessException("Ordinal failure (#" + ordinal + ") @ " + element.getClass().getTypeName() + " : " + '"' + e.getCause().getMessage() + '"', e.getCause().getStackTrace());
						}
					} else {
						throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getSimpleName());
					}
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
					if (e.getCause() != null) {
						if (e.getCause().getMessage() == null) {
							throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getTypeName(), e.getCause().getStackTrace());
						} else {
							throw new OrdinalProcessException("Ordinal failure (#" + ordinal + ") @ " + element.getClass().getTypeName() + " : " + '"' + e.getCause().getMessage() + '"', e.getCause().getStackTrace());
						}
					} else {
						throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getSimpleName());
					}
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
				if (exception.getCause() != null) {
					if (exception.getCause().getMessage() == null) {
						throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getTypeName(), exception.getCause().getStackTrace());
					} else {
						throw new OrdinalProcessException("Ordinal failure (#" + ordinal + ") @ " + element.getClass().getTypeName() + " : " + '"' + exception.getCause().getMessage() + '"', exception.getCause().getStackTrace());
					}
				} else {
					throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getSimpleName());
				}
			}
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
				if (exception.getCause() != null) {
					if (exception.getCause().getMessage() == null) {
						throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getTypeName(), exception.getCause().getStackTrace());
					} else {
						throw new OrdinalProcessException("Ordinal failure (#" + ordinal + ") @ " + element.getClass().getTypeName() + " : " + '"' + exception.getCause().getMessage() + '"', exception.getCause().getStackTrace());
					}
				} else {
					throw new OrdinalProcessException("Ordinal processing failed on #" + ordinal + " for object " + element.getClass().getSimpleName());
				}
			}
		}).orElseThrow(() -> new RuntimeException("Ordinal " + ordinal + " either not found or access failed."));
	}

}