package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;

public abstract class LabyrinthProvider {
	static LabyrinthAPI instance;

	private LabyrinthProvider() {
		throw new IllegalStateException("This class should never be instantiated!");
	}

	/**
	 * Get the registered instance of the Labyrinth API.
	 *
	 * @return instance of the Labyrinth API
	 */
	public static LabyrinthAPI getInstance() {
		return instance;
	}

	/**
	 * Get an extended api instance with quick menu creation access.
	 * <p>
	 * Ex. {@link MenuOverride}
	 *
	 * @param api The override to use.
	 * @param <R> The desired API type.
	 * @return instance of the Labyrinth API
	 */
	public static <R extends LabyrinthAPI> R getInstance(Class<R> api) {
		return (R) instance;
	}

}
