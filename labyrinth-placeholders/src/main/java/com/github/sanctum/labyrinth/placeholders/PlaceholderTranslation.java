package com.github.sanctum.labyrinth.placeholders;

import com.github.sanctum.labyrinth.library.Deployable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for converting placeholder parameters into any string of choice.
 */
@FunctionalInterface
public interface PlaceholderTranslation {

	@Nullable String onTranslation(String parameter, PlaceholderVariable variable);

	/**
	 * Get the creator information for this implementation.
	 *
	 * @return The information for this translation or null.
	 */
	@Nullable default PlaceholderTranslationInformation getInformation() {
		return null;
	}

	/**
	 * Get all the placeholders this implementation uses.
	 *
	 * @return an array of placeholders used with this translation.
	 */
	@NotNull
	default Placeholder[] getPlaceholders() {
		return Placeholder.values();
	}

	/**
	 * Get this translation's special identifier if it has one.
	 *
	 * @return the special identifier for this translation or null.
	 */
	@Nullable
	default PlaceholderIdentifier getIdentifier() {
		return null;
	}

	/**
	 * Get all found (registered) placeholders within the given text.
	 *
	 * @param text the text to search.
	 * @return an array of placeholders or empty if none.
	 */
	default Placeholder[] getPlaceholders(String text) {
		return PlaceholderRegistration.instance.getPlaceholders(text, this);
	}

	/**
	 * Check if this translation uses a special identifier.
	 *
	 * @return false if this identifier is null.
	 */
	default boolean hasCustomIdentifier() {
		return getIdentifier() != null;
	}

	/**
	 * Check if this translation is registered.
	 *
	 * @return true if this translation is registered into cache.
	 */
	default boolean isRegistered() {
		return PlaceholderRegistration.getInstance().isRegistered(this);
	}

	/**
	 * Register this translation to cache.
	 *
	 * @return A deployable registration sequence.
	 */
	default Deployable<PlaceholderTranslation> register() {
		return PlaceholderRegistration.getInstance().registerTranslation(this);
	}

	/**
	 * Remove this translation from cache.
	 *
	 * @return A deployable removal sequence.
	 */
	default Deployable<PlaceholderTranslation> unregister() {
		return PlaceholderRegistration.getInstance().unregisterTranslation(this);
	}



}
