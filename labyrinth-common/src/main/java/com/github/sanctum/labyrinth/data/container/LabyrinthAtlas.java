package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.data.MemorySpace;
import java.util.Map;

public interface LabyrinthAtlas extends MemorySpace, LabyrinthMap<String, Object> {

	/**
	 * Since the memory space for this map isn't persistent, there is no visible path.
	 */
	@Override
	@Note("Always null!")
	default String getPath() {
		return null;
	}

}
