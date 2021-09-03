package com.github.sanctum.labyrinth.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * An interface dedicated to writing keyed objects to a {@link FileManager}
 *
 * @author Hempfest
 * @version 1.0
 */
public interface DataMap {

	<T> DataMap set(String key, T value);

	DataMap removeAdded(Predicate<String> pred);

	default Map<String, Object> get() {
		return new HashMap<>();
	}

	static DataMap newMap() {
		return new LabyrinthDataMap();
	}

}
