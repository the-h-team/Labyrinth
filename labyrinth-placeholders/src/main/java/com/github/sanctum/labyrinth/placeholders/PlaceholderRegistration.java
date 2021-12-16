package com.github.sanctum.labyrinth.placeholders;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.github.sanctum.labyrinth.library.Deployable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An abstraction dedicated to managing placeholder translation requests & additions.
 */
public abstract class PlaceholderRegistration {

	static PlaceholderTranslationUtility instance;
	static final LabyrinthMap<String, Map<String, Placeholder>> history = new LabyrinthEntryMap<>();


	public static @NotNull
	@Note("Always valid!")
	PlaceholderRegistration getInstance() {
		if (instance == null) {
			instance = new PlaceholderTranslationUtility() {
			};
		}
		return instance.registration;
	}

	/**
	 * Register a custom placeholder translation into cache.
	 *
	 * @param translation The translation to register.
	 * @return A deployable registration sequence.
	 */
	public abstract Deployable<PlaceholderTranslation> registerTranslation(@NotNull PlaceholderTranslation translation);

	/**
	 * Register a custom placeholder translation into cache from file.
	 *
	 * @param file The file to use for loading.
	 * @return A deployable registration sequence.
	 */
	public abstract Deployable<PlaceholderTranslation> registerTranslation(@NotNull File file);

	/**
	 * Remove a registered translation from cache.
	 *
	 * @param translation The translation to remove.
	 * @return A deployable removal sequence.
	 */
	public abstract Deployable<PlaceholderTranslation> unregisterTranslation(@NotNull PlaceholderTranslation translation);

	/**
	 * Get a registered translation by its identifier.
	 *
	 * @param identifier The identifier to use.
	 * @return The registered translation or null.
	 */
	public abstract PlaceholderTranslation getTranslation(@NotNull PlaceholderIdentifier identifier);

	/**
	 * Get a registered translation by its name.
	 *
	 * @param name The identifier to use.
	 * @return The registered translation or null.
	 */
	public abstract PlaceholderTranslation getTranslation(@NotNull String name);

	/**
	 * A forEach method on cached translation objects.
	 *
	 * @param consumer The consuming operation to enact.
	 */
	public abstract void runAction(@NotNull Consumer<PlaceholderTranslation> consumer);

	/**
	 * Check if a specific translation is registered into cache.
	 *
	 * @param translation The translation to check
	 * @return true if the translation is registered.
	 */
	public abstract boolean isRegistered(@NotNull PlaceholderTranslation translation);

	/**
	 * Check if a string contains any registered placeholders.
	 *
	 * @param text The string to check
	 * @return false if the string contains placeholders.
	 */
	public abstract boolean isEmpty(@NotNull String text);

	/**
	 * Check if a string contains any registered placeholders under the specified identity.
	 *
	 * @param text       The string to check
	 * @param identifier The identity
	 * @return false if the string contains placeholders regarding the specified identity.
	 */
	public abstract boolean isEmpty(@NotNull String text, @Nullable PlaceholderIdentifier identifier);

	/**
	 * Replace all possible placeholders in the provided string.
	 *
	 * @param text The string to format
	 * @return A placeholder formatted string.
	 */
	public abstract @NotNull String replaceAll(@NotNull String text);

	/**
	 * Replace all possible placeholders in the provided string using a custom variable.
	 *
	 * @param text     The string to format
	 * @param receiver The variable to provide for context
	 * @return A placeholder formatted string.
	 */
	public abstract @NotNull String replaceAll(@NotNull String text, @Nullable Object receiver);

	/**
	 * Replace all possible placeholders in the provided string using a custom variable
	 *
	 * @param text     The string to format
	 * @param receiver The variable to provide for context
	 * @return A placeholder formatted string
	 */
	public abstract @NotNull String replaceAll(@NotNull String text, @Nullable PlaceholderVariable receiver);

	/**
	 * Replace all possible placeholders in the provided string using a custom variable and placeholder inquiry.
	 *
	 * @param text        The string to format
	 * @param receiver    The variable to provide for context
	 * @param placeholder The placeholder to format
	 * @return A placeholder formatted string.
	 */
	public abstract @NotNull String replaceAll(@NotNull String text, @Nullable PlaceholderVariable receiver, Placeholder placeholder);

	/**
	 * Replace all possible placeholders in the provided string using a custom variable, placeholder inquiry & identity.
	 *
	 * @param text        The string to format
	 * @param receiver    The variable to provide for context
	 * @param identifier  The identity
	 * @param placeholder The placeholder to format
	 * @return A placeholder formatted string.
	 */
	public abstract @NotNull String replaceAll(@NotNull String text, @Nullable PlaceholderVariable receiver, @Nullable PlaceholderIdentifier identifier, @NotNull Placeholder placeholder);

	public abstract @NotNull String replaceAll(@NotNull String text, @NotNull Placeholder placeholder, @NotNull String replacement);

	public abstract @Nullable String findFirst(@NotNull String text, @NotNull Placeholder placeholder);

	public abstract @NotNull LabyrinthCollection<String> findAny(@NotNull String text, @NotNull Placeholder placeholder);

	public final LabyrinthMap<PlaceholderIdentifier, List<Placeholder>> getHistory() {
		LabyrinthMap<PlaceholderIdentifier, List<Placeholder>> map = new LabyrinthEntryMap<>();
		history.forEach(entry -> {
			PlaceholderIdentifier identifier = entry::getKey;
			List<Placeholder> placeholders = new ArrayList<>(entry.getValue().values());
			map.put(identifier, placeholders);
		});
		return map;
	}


}
