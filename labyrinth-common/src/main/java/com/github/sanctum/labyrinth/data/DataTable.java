package com.github.sanctum.labyrinth.data;

import java.util.Map;

/**
 * An interface dedicated to writing keyed objects to a {@link FileManager}
 *
 * @author Hempfest
 * @version 1.0
 */
public interface DataTable {

	<T> DataTable set(String key, T value);

	void clear();

	Map<String, Object> values();

	static DataTable newTable() {
		return new LabyrinthDataTable();
	}

}
