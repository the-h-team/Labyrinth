package com.github.sanctum.labyrinth.api;

import com.github.sanctum.panther.annotation.Removal;
import org.jetbrains.annotations.NotNull;

@Removal
public interface PlaceholderFormatService extends Service {

	@NotNull String replaceAll(@NotNull String text, Object variable);

	default @NotNull String replaceEverything(@NotNull String text, Object... variables) {
		for (Object o : variables) {
			text = replaceAll(text, o);
		}
		return text;
	}

}
