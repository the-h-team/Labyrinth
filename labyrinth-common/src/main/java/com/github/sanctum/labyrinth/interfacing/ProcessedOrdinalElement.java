package com.github.sanctum.labyrinth.interfacing;

final class ProcessedOrdinalElement<E> implements OrdinalElement<E> {

	private final E element;

	ProcessedOrdinalElement(E element) {
		this.element = element;
	}

	@Override
	public E getElement() {
		return element;
	}

	@Override
	public GenericOrdinalElement select(int ordinal) {
		return OrdinalProcedure.select(getElement(), ordinal);
	}

	@Override
	public GenericOrdinalElement select(int ordinal, Object... args) {
		return OrdinalProcedure.select(getElement(), ordinal, args);
	}

	@Override
	public String toString() {
		return getElement().toString();
	}
}
