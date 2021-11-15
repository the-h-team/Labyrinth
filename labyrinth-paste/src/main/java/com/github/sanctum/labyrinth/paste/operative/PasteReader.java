package com.github.sanctum.labyrinth.paste.operative;

/**
 * Signifies an object that can read information from the web using a string id.
 */
public interface PasteReader {

	/**
	 * Read information from the web using a key.
	 *
	 * @param id The key destination.
	 * @return a response from the web.
	 */
	PasteResponse read(String id);

}
