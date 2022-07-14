package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.data.AtlasMap;
import org.jetbrains.annotations.NotNull;

public class MapDocket<T> extends MemoryDocket<T> {

	public MapDocket(@NotNull AtlasMap map) {
		super(map);
	}
}
