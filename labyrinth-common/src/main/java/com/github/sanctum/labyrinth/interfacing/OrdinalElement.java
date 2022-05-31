package com.github.sanctum.labyrinth.interfacing;

/**
 * This object represents that of a numerable object. Something that can be divisively accessible.
 *
 * @param <E> The type of element.
 */
public interface OrdinalElement<E> {

	/**
	 * @return The initial source element.
	 */
	E getElement();

	/**
	 * Select a generic ordinal from this element.
	 *
	 * @param ordinal The ordinal to select.
	 * @return A generic ordinal.
	 */
	GenericOrdinalElement select(int ordinal);

	/**
	 * Select a generic ordinal from this element.
	 *
	 * @param ordinal The ordinal to select.
	 * @param args The variable args to use if required.
	 * @return A generic ordinal.
	 */
	GenericOrdinalElement select(int ordinal, Object... args);

	default GenericOrdinalElement toGeneric() {
		return this instanceof GenericOrdinalElement ? (GenericOrdinalElement) this : null;
	}

}
