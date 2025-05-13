package com.github.sanctum.labyrinth.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Deprecated
@ApiStatus.ScheduledForRemoval
public interface PlaceholderFormatService extends Service {

	@NotNull String replaceAll(@NotNull String text, Object variable);

	default @NotNull String replaceEverything(@NotNull String text, Object... variables) {
		for (Object o : variables) {
			text = replaceAll(text, o);
		}
		return text;
	}

}
