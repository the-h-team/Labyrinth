package com.github.sanctum.labyrinth.placeholders;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PlaceholderIdentifier {

	@NotNull String get();

	default @NotNull String spacer() {
		return "_";
	}

}
