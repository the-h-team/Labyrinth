package com.github.sanctum.labyrinth.placeholders;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.AddonLoader;
import com.github.sanctum.labyrinth.library.Deployable;
import com.github.sanctum.labyrinth.library.StringUtils;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class PlaceholderTranslationUtility {

	final PlaceholderRegistration registration;

	PlaceholderTranslationUtility() {
		this.registration = new PlaceholderRegistration() {

			private final List<PlaceholderTranslation> translations = Collections.synchronizedList(new LinkedList<>());

			@Override
			public Deployable<PlaceholderTranslation> registerTranslation(@NotNull PlaceholderTranslation translation) {
				return Deployable.of(translation, translations::add);
			}

			@Override
			public Deployable<PlaceholderTranslation> registerTranslation(@NotNull File file) {
				AddonLoader loader = AddonLoader.forPlugin(LabyrinthProvider.getInstance().getPluginInstance());
				List<Class<?>> classes = loader.loadFile(file);
				if (classes.stream().noneMatch(PlaceholderTranslation.class::isAssignableFrom))
					throw new IllegalArgumentException("No translation found from the specified file!");
				PlaceholderTranslation translation = classes.stream().filter(PlaceholderTranslation.class::isAssignableFrom).map(aClass -> {
					try {
						return (PlaceholderTranslation) aClass.getDeclaredConstructor().newInstance();
					} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
					return null;
				}).findFirst().orElse(null);
				return Deployable.of(translation, translations::add);
			}

			@Override
			public Deployable<PlaceholderTranslation> unregisterTranslation(@NotNull PlaceholderTranslation translation) {
				return Deployable.of(translation, translations::remove);
			}

			@Override
			public PlaceholderTranslation getTranslation(@NotNull PlaceholderIdentifier identifier) {
				return translations.stream().filter(placeholderTranslation -> placeholderTranslation.hasCustomIdentifier() && placeholderTranslation.getIdentifier().get().equals(identifier.get())).findFirst().orElse(null);
			}

			@Override
			public PlaceholderTranslation getTranslation(@NotNull String name) {
				return translations.stream().filter(translation -> translation.getInformation() != null && translation.getInformation().getName().equals(name)).findFirst().orElse(null);
			}

			@Override
			public void runAction(@NotNull Consumer<PlaceholderTranslation> consumer) {
				translations.forEach(consumer);
			}

			@Override
			public boolean isRegistered(@NotNull PlaceholderTranslation translation) {
				return translations.contains(translation);
			}

			@Override
			public boolean isEmpty(@NotNull String text) {
				return isEmpty(text, null);
			}

			@Override
			public boolean isEmpty(@NotNull String text, @Nullable PlaceholderIdentifier identifier) {
				boolean empty = false;
				Iterator<PlaceholderTranslation> conversionIterator = translations.listIterator();
				do {
					PlaceholderTranslation conversion = conversionIterator.next();
					for (Placeholder hold : conversion.getPlaceholders()) {
						empty = PlaceholderTranslationUtility.this.isEmpty(text, identifier != null ? identifier : conversion.getIdentifier(), hold);
					}
				} while (conversionIterator.hasNext());
				return empty;
			}

			@Override
			public @NotNull String replaceAll(@NotNull String text) {
				if (translations.isEmpty()) return text;
				return replaceAll(text, null);
			}

			@Override
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

			@Override
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

			@Override
			public @NotNull String replaceAll(@NotNull String text, @Nullable PlaceholderVariable receiver, Placeholder placeholder) {
				if (translations.isEmpty()) return text;
				return replaceAll(text, receiver, null, placeholder);
			}

			@Override
			public @NotNull String replaceAll(@NotNull String text, @Nullable PlaceholderVariable receiver, @Nullable PlaceholderIdentifier identifier, @NotNull Placeholder placeholder) {
				if (translations.isEmpty()) return text;
				return PlaceholderTranslationUtility.this.getTranslation(text, identifier, receiver, placeholder);
			}
		};
	}

	boolean isEmpty(String text, PlaceholderIdentifier identifier, Placeholder placeholder) {
		Pattern pattern = getPattern(identifier, placeholder);
		Matcher matcher = pattern.matcher(text);
		return !matcher.find();
	}

	@NotNull String getTranslation(String text, PlaceholderIdentifier identifier, PlaceholderVariable receiver, Placeholder placeholder) {
		Pattern pattern = getPattern(identifier, placeholder);
		Matcher matcher = pattern.matcher(text);
		if (!matcher.find())
			return text;
		StringBuffer builder = new StringBuffer();
		do {
			if (identifier != null) {
				String id = matcher.group("identifier");
				PlaceholderTranslation conversion = PlaceholderRegistration.getInstance().getTranslation(() -> id);
				String parameters = matcher.group("parameters");
				String translation = conversion.onTranslation(parameters, receiver != null ? receiver : () -> null);
				matcher.appendReplacement(builder, translation != null ? translation : (placeholder.start() + identifier.get() + identifier.spacer() + parameters + placeholder.end()));
			} else {
				PlaceholderRegistration.getInstance().runAction(conversion -> {
					String parameters = matcher.group("parameters");
					String translation = conversion.onTranslation(parameters, receiver != null ? receiver : () -> null);
					matcher.appendReplacement(builder, translation != null ? translation : (placeholder.start() + parameters + placeholder.end()));
				});
			}
		} while (matcher.find());
		return StringUtils.use(matcher.appendTail(builder).toString()).translate();
	}

	@NotNull Placeholder[] getPlaceholders(String text, PlaceholderTranslation conversion) {
		List<Placeholder> placeholderList = new ArrayList<>();
		for (Placeholder p : conversion.getPlaceholders()) {
			Pattern pattern = getPattern(conversion.getIdentifier(), p);
			Matcher matcher = pattern.matcher(text);
			if (!matcher.find()) continue;
			do {
				String parameters = matcher.group("parameters");
				placeholderList.add(new Placeholder() {
					@Override
					public char start() {
						return p.start();
					}

					@Override
					public CharSequence parameters() {
						return parameters;
					}

					@Override
					public char end() {
						return p.end();
					}
				});
			} while (matcher.find());
		}
		return placeholderList.toArray(new Placeholder[0]);
	}

	@NotNull Pattern getPattern(PlaceholderIdentifier identifier, Placeholder placeholder) {
		Pattern pattern;
		if (identifier == null) {
			pattern = Pattern.compile(String.format("\\%s(?<parameters>[^%s%s]+)\\%s", placeholder.start(),
					placeholder.start(), placeholder.end(), placeholder.end()));
		} else {
			pattern = Pattern.compile(String.format("\\%s((?<identifier>[a-zA-Z0-9]+)" + identifier.spacer() + ")(?<parameters>[^%s%s]+)\\%s", placeholder.start(),
					placeholder.start(), placeholder.end(), placeholder.end()));
		}
		return pattern;
	}

}
