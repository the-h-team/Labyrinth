package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.data.MemorySpace;
import java.util.Map;

public class MapDocket<T> extends MemoryDocket<T> {

	public MapDocket(Map<String, Object> map) {
		super(MemorySpace.wrapNonLinked(map));
	}
}
