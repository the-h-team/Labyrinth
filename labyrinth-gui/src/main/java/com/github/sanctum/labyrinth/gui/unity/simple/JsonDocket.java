package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.data.AtlasMap;
import com.github.sanctum.panther.annotation.Json;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class JsonDocket<T> extends MapDocket<T> {

	public JsonDocket(@NotNull @Json String json, @NotNull Function<String, AtlasMap> function) {
		super(function.apply(json));
	}

}
