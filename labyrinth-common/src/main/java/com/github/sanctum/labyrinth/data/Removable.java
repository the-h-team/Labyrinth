package com.github.sanctum.labyrinth.data;

/**
 * An interface marking objects that have both removable and savable states.
 *
 * @author Hempfest
 * @version 1.0
 */
public interface Removable {

	/**
	 * Save the configuration to its backing file.
	 *
	 * @throws java.io.IOException if an error is encountered while saving
	 */
	boolean save();

	/**
	 * Delete the backing location.
	 *
	 * <p>Does not destroy backing file if this object is representative of {@link Node}</p>
	 *
	 * <p>If this object is representative of {@link Configurable} the parent file will attempt removal.</p>
	 *
	 * @return true if the backing location was removed.
	 */
	boolean delete();

}
