package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.data.MemorySpace;
import org.jetbrains.annotations.NotNull;

public class JsonDocket<T> extends MemoryDocket<T> {

	public JsonDocket(@NotNull @Json String json) {
		super(MemorySpace.wrapNonLinked(json));
	}

}
