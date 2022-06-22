package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.annotation.Note;
import java.util.Map;

public interface Atlas extends MemorySpace, Map<String, Object> {

	/**
	 * Since the memory space for this map isn't persistent, there is no visible path.
	 */
	@Override
	@Note("Always null!")
	default String getPath() {
		return null;
	}

	char getDivider();

}
