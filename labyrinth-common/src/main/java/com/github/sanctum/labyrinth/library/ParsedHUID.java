package com.github.sanctum.labyrinth.library;

/**
 * An {@link HUID} parser, check validity of a known id or make a new one.
 */
public interface ParsedHUID {

	/**
	 * @return true if this 'id' is valid.
	 */
	boolean isValid();

	/**
	 * Get the id from this transaction.
	 *
	 * @return the id under this memory space.
	 */
	HUID toID();

	/**
	 * Create a new id using the hash identity of the referenced value.
	 *
	 * @return a new id made from the referenced hash identity.
	 */
	HUID newID();

	/**
	 * Create a new id using the hash identity of the referenced value.
	 *
	 * @param characters The assortment of characters to use for generation.
	 * @return a new id made from the referenced hash identity.
	 */
	HUID newID(Character... characters);

	@Override
	String toString();

}
