package com.github.sanctum.labyrinth.data.service;

import org.jetbrains.annotations.NotNull;

public interface PlaceholderFormatService extends Service {

	@NotNull String replaceAll(@NotNull String text, Object variable);

	default @NotNull String replaceAnything(@NotNull String text, Object... variables) {
		for (Object o : variables) {
			text = replaceAll(text, o);
		}
		return text;
	}

}