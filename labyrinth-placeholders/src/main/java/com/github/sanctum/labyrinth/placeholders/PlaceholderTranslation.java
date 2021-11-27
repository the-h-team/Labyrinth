package com.github.sanctum.labyrinth.placeholders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PlaceholderTranslation {

	@Nullable String onTranslation(String parameter, PlaceholderVariable variable);

	@NotNull
	default Placeholder[] getPlaceholders() {
		return new Placeholder[]{Placeholder.CURLEY_BRACKETS};
	}

	@Nullable
	default PlaceholderIdentifier getIdentifier() {
		return null;
	}

	default Placeholder[] getPlaceholders(String text) {
		return PlaceholderTranslationUtility.placeholders(text, this);
	}

	default boolean hasCustomIdentifier() {
		return getIdentifier() != null;
	}

}
