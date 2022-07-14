package com.github.sanctum.labyrinth.data.container;


import com.github.sanctum.panther.annotation.Note;
import com.github.sanctum.panther.container.PantherMap;
import com.github.sanctum.panther.file.MemorySpace;

public interface LabyrinthAtlas extends MemorySpace, PantherMap<String, Object> {

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
