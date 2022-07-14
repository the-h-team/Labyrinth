package com.github.sanctum.labyrinth.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A labyrinth provided implementation of a data table service.
 *
 * @author Hempfest
 * @version 1.0
 */
public final class LabyrinthDataTable implements DataTable {

	final Map<String, Object> tempSpace = new HashMap<>();

	@Override
	public <T> LabyrinthDataTable set(String key, T value) {
		if (value == null) {
			tempSpace.put(key, "NULL");
		} else {
			tempSpace.put(key, value);
		}
		return this;
	}

	@Override
	public void clear() {
		tempSpace.clear();
	}

	@Override
	public Map<String, Object> values() {
		return Collections.unmodifiableMap(tempSpace);
	}
}
