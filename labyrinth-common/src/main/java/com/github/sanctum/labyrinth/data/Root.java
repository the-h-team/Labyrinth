package com.github.sanctum.labyrinth.data;

import java.io.IOException;

public interface Root {

	/**
	 * Save the following memory space to its backing file.
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
	 * <p>If the backing of this object is a {@link Node} perhaps the desired form of removal is {@code Node.set(null)} as attempting
	 * to delete a node that is not a directory will result in a soft failure</p>
	 *
	 * @return true if the backing location was removed.
	 */
	boolean delete();

	/**
	 * Reload the file from disk.
	 * <p>
	 * If the backing file has been deleted, this method assigns a fresh,
	 * blank configuration internally to this object. Otherwise, the file
	 * is read from, directly replacing the existing configuration with
	 * its values. No attempt is made to save the existing configuration
	 * state, so keep that in mind when running this call.
	 */
	void reload();

	/**
	 * Attempt creating the file location.
	 * <p>
	 * If the parent location doesn't exist (The backing location for our file)
	 * One will be created before attempting file creation.
	 *
	 * @return true if creation was successful
	 */
	boolean create() throws IOException;

	/**
	 * Check if the backing file is currently existent.
	 * <p>
	 * Does not interact whatsoever with the internal implementation.
	 *
	 * @return true if file found
	 */
	boolean exists();

}
