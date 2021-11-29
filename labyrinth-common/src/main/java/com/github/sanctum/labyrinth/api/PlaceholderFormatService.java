package com.github.sanctum.labyrinth.api;

import org.jetbrains.annotations.NotNull;

public interface PlaceholderFormatService extends Service {

	@NotNull String replaceAll(@NotNull String text, Object variable);

}
