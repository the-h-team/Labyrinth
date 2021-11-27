package com.github.sanctum.labyrinth.placeholders;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.data.AddonLoader;
import com.github.sanctum.labyrinth.library.Deployable;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlaceholderRegistration {

	private static PlaceholderRegistration instance;
	private final List<PlaceholderTranslation> translations = Collections.synchronizedList(new LinkedList<>());

	PlaceholderRegistration() {
		instance = this;
	}

	public static @NotNull @Note("Always valid!") PlaceholderRegistration getInstance() {
		return instance != null ? instance : new PlaceholderRegistration() {
		};
	}

	public Deployable<PlaceholderTranslation> registerTranslation(@NotNull PlaceholderTranslation translation) {
		return Deployable.of(translation, translations::add);
	}

	public Deployable<PlaceholderTranslation> registerTranslation(@NotNull File file) {
		AddonLoader loader = AddonLoader.forPlugin(LabyrinthProvider.getInstance().getPluginInstance());
		List<Class<?>> classes = loader.loadFile(file);
		if (classes.stream().noneMatch(PlaceholderTranslation.class::isAssignableFrom)) throw new IllegalArgumentException("No translation found from the specified file!");
		PlaceholderTranslation translation = classes.stream().filter(PlaceholderTranslation.class::isAssignableFrom).map(aClass -> {
			try {
				return (PlaceholderTranslation)aClass.getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		}).findFirst().orElse(null);
		return Deployable.of(translation, translations::add);
	}

	public Deployable<PlaceholderTranslation> unregisterTranslation(@NotNull PlaceholderTranslation translation) {
		return Deployable.of(translation, translations::remove);
	}

	public PlaceholderTranslation getTranslation(@NotNull PlaceholderIdentifier identifier) {
		return translations.stream().filter(placeholderTranslation -> placeholderTranslation.hasCustomIdentifier() && placeholderTranslation.getIdentifier().get().equals(identifier.get())).findFirst().orElse(null);
	}

	public void runAction(@NotNull Consumer<PlaceholderTranslation> consumer) {
		translations.forEach(consumer);
	}

	public boolean isEmpty(@NotNull String text) {
		return isEmpty(text, null);
	}

	public boolean isEmpty(@NotNull String text, @Nullable PlaceholderIdentifier identifier) {
		boolean empty = false;
		Iterator<PlaceholderTranslation> conversionIterator = translations.listIterator();
		do {
			PlaceholderTranslation conversion = conversionIterator.next();
			for (Placeholder hold : conversion.getPlaceholders()) {
				empty = PlaceholderTranslationUtility.isEmpty(text, identifier != null ? identifier : conversion.getIdentifier(), hold);
			}
		} while (conversionIterator.hasNext());
		return empty;
	}

	public @NotNull String replaceAll(@NotNull String text) {
		if (translations.isEmpty()) return text;
		return replaceAll(text, null);
	}

	public @NotNull String replaceAll(@NotNull String text, @Nullable Object receiver) {
		if (translations.isEmpty()) return text;
		String result = text;
		Iterator<PlaceholderTranslation> conversionIterator = translations.listIterator();
		do {
			PlaceholderTranslation conversion = conversionIterator.next();
			for (Placeholder hold : conversion.getPlaceholders()) {
				result = replaceAll(result, () -> receiver, conversion.getIdentifier(), hold);
			}
		} while (conversionIterator.hasNext());
		return result;
	}

	public @NotNull String replaceAll(@NotNull String text, @Nullable PlaceholderVariable receiver) {
		if (translations.isEmpty()) return text;
		String result = text;
		Iterator<PlaceholderTranslation> conversionIterator = translations.listIterator();
		do {
			PlaceholderTranslation conversion = conversionIterator.next();
			for (Placeholder hold : conversion.getPlaceholders()) {
				result = replaceAll(result, receiver, conversion.getIdentifier(), hold);
			}
		} while (conversionIterator.hasNext());
		return result;
	}

	public @NotNull String replaceAll(@NotNull String text, @Nullable PlaceholderVariable receiver, Placeholder placeholder) {
		if (translations.isEmpty()) return text;
		return replaceAll(text, receiver, null, placeholder);
	}

	public @NotNull String replaceAll(@NotNull String text, @Nullable PlaceholderVariable receiver, @Nullable PlaceholderIdentifier identifier, @NotNull Placeholder placeholder) {
		if (translations.isEmpty()) return text;
		return PlaceholderTranslationUtility.translate(text, identifier, receiver, placeholder);
	}



}
