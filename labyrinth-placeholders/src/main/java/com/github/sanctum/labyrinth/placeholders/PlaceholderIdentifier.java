package com.github.sanctum.labyrinth.placeholders;

import org.jetbrains.annotations.NotNull;

/**
 * An interface  describing a primary identifier for a placeholder translation.
 */
@FunctionalInterface
public interface PlaceholderIdentifier {

	@NotNull String get();

	default @NotNull String spacer() {
		return "_";
	}

}
