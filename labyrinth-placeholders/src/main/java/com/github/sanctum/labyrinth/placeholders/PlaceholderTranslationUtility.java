package com.github.sanctum.labyrinth.placeholders;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.library.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Note("A private delegation class! Util only! (Regex formula taken from PlaceholderAPI)")
final class PlaceholderTranslationUtility {

	public static boolean isEmpty(String text, PlaceholderIdentifier identifier, Placeholder placeholder) {
		Pattern pattern;
		if (identifier == null) {
			pattern = Pattern.compile(String.format("\\%s(?<parameters>[^%s%s]+)\\%s", placeholder.start(),
					placeholder.start(), placeholder.end(), placeholder.end()));
		} else {
			pattern = Pattern.compile(String.format("\\%s((?<identifier>[a-zA-Z0-9]+)" + identifier.spacer() + ")(?<parameters>[^%s%s]+)\\%s", placeholder.start(),
					placeholder.start(), placeholder.end(), placeholder.end()));
		}
		Matcher matcher = pattern.matcher(text);
		return !matcher.find();
	}

	public static String translate(String text, PlaceholderIdentifier identifier, PlaceholderVariable receiver, Placeholder placeholder) {
		if (receiver == null) {
			receiver = () -> null;
		}
		Pattern pattern;
		if (identifier == null) {
			pattern = Pattern.compile(String.format("\\%s(?<parameters>[^%s%s]+)\\%s", placeholder.start(),
					placeholder.start(), placeholder.end(), placeholder.end()));
		} else {
			pattern = Pattern.compile(String.format("\\%s((?<identifier>[a-zA-Z0-9]+)" + identifier.spacer() + ")(?<parameters>[^%s%s]+)\\%s", placeholder.start(),
					placeholder.start(), placeholder.end(), placeholder.end()));
		}
		Matcher matcher = pattern.matcher(text);
		if (!matcher.find())
			return text;
		StringBuffer builder = new StringBuffer();
		do {
			if (identifier != null) {
				String id = matcher.group("identifier");
				PlaceholderTranslation conversion = PlaceholderRegistration.getInstance().getTranslation(() -> id);
				String parameters = matcher.group("parameters");
				String requested = conversion.onTranslation(parameters, receiver);
				matcher.appendReplacement(builder, requested != null ? requested : (placeholder.start() + identifier.get() + identifier.spacer() + parameters + placeholder.end()));
			} else {
				PlaceholderVariable finalReceiver = receiver;
				PlaceholderRegistration.getInstance().runAction(conversion -> {
					String parameters = matcher.group("parameters");
					String requested = conversion.onTranslation(parameters, finalReceiver);
					matcher.appendReplacement(builder, requested != null ? requested : (placeholder.start() + parameters + placeholder.end()));
				});
			}
		} while (matcher.find());
		return StringUtils.use(matcher.appendTail(builder).toString()).translate();
	}

	public static Placeholder[] placeholders(String text, PlaceholderTranslation conversion) {
		Pattern pattern;
		if (conversion.getIdentifier() == null) {
			pattern = Pattern.compile(String.format("\\%s(?<parameters>[^%s%s]+)\\%s", conversion.getPlaceholders()[0].start(),
					conversion.getPlaceholders()[0].start(), conversion.getPlaceholders()[0].end(), conversion.getPlaceholders()[0].end()));
		} else {
			pattern = Pattern.compile(String.format("\\%s((?<identifier>[a-zA-Z0-9]+)" + conversion.getIdentifier().spacer() + ")(?<parameters>[^%s%s]+)\\%s", conversion.getPlaceholders()[0].start(),
					conversion.getPlaceholders()[0].start(), conversion.getPlaceholders()[0].end(), conversion.getPlaceholders()[0].end()));
		}
		Matcher matcher = pattern.matcher(text);
		if (!matcher.find())
			return new Placeholder[0];
		List<Placeholder> placeholderList = new ArrayList<>();
		do {
			String parameters = matcher.group("parameters");
			placeholderList.add(new Placeholder() {
				@Override
				public char start() {
					return conversion.getPlaceholders()[0].start();
				}

				@Override
				public CharSequence parameters() {
					return parameters;
				}

				@Override
				public char end() {
					return conversion.getPlaceholders()[0].end();
				}
			});
		} while (matcher.find());
		return placeholderList.toArray(new Placeholder[0]);
	}

}
