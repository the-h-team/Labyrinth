package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.PlaceholderFormatService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public final class FormattedString {

	private final String context;
	private String formatted;

	public FormattedString(@NotNull String context) {
		this.context = context;
		this.formatted = context;
	}

	public FormattedString append(char c) {
		this.formatted += String.valueOf(c);
		return this;
	}

	public FormattedString append(@NotNull CharSequence sequence) {
		this.formatted += sequence.toString();
		return this;
	}

	public FormattedString append(@NotNull Number number) {
		if (number instanceof Double) {
			this.formatted += number.doubleValue();
		}
		if (number instanceof Long) {
			this.formatted += number.longValue();
		}
		if (number instanceof Integer) {
			this.formatted += number.intValue();
		}
		if (number instanceof Short) {
			this.formatted += number.shortValue();
		}
		if (number instanceof Float) {
			this.formatted += number.floatValue();
		}
		if (number instanceof Byte) {
			this.formatted += number.byteValue();
		}
		return this;
	}

	public FormattedString color() {
		this.formatted = new ColoredString(formatted).toString();
		return this;
	}

	public FormattedString translate() {
		this.formatted = LabyrinthProvider.getService(PlaceholderFormatService.class).replaceAll(context, null);
		return this;
	}

	public FormattedString translate(@NotNull Object variable) {
		this.formatted = LabyrinthProvider.getService(PlaceholderFormatService.class).replaceAll(context, variable);
		return this;
	}

	public FormattedString replace(@NotNull String regex, @NotNull String replacement) {
		this.formatted = Pattern.compile(regex, Pattern.LITERAL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(formatted)
				.replaceAll(Matcher.quoteReplacement(replacement));
		return this;
	}

	public boolean contains(CharSequence... sequences) {
		for (CharSequence s : sequences) {
			if (Pattern.compile(Pattern.quote(s.toString()), Pattern.CASE_INSENSITIVE).matcher(this.context).find()) return true;
		}
		return false;
	}

	public boolean containsAll(CharSequence... sequences) {
		for (CharSequence s : sequences) {
			if (!Pattern.compile(Pattern.quote(s.toString()), Pattern.CASE_INSENSITIVE).matcher(this.context).find()) return false;
		}
		return true;
	}

	public String get() {
		return formatted;
	}

}
