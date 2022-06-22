package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.PlaceholderFormatService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public final class FormattedString {

	private String context;

	public FormattedString(@NotNull String context) {
		this.context = context;
	}

	public FormattedString append(char c) {
		this.context += String.valueOf(c);
		return this;
	}

	public FormattedString append(@NotNull CharSequence sequence) {
		this.context += sequence.toString();
		return this;
	}

	public FormattedString append(@NotNull Number number) {
		if (number instanceof Double) {
			this.context += number.doubleValue();
		}
		if (number instanceof Long) {
			this.context += number.longValue();
		}
		if (number instanceof Integer) {
			this.context += number.intValue();
		}
		if (number instanceof Short) {
			this.context += number.shortValue();
		}
		if (number instanceof Float) {
			this.context += number.floatValue();
		}
		if (number instanceof Byte) {
			this.context += number.byteValue();
		}
		return this;
	}

	public FormattedString color() {
		this.context = new ColoredString(context).toString();
		return this;
	}

	public FormattedString translate() {
		this.context = LabyrinthProvider.getService(PlaceholderFormatService.class).replaceAll(context, null);
		return this;
	}

	public FormattedString translate(@NotNull Object variable) {
		this.context = LabyrinthProvider.getService(PlaceholderFormatService.class).replaceAll(context, variable);
		return this;
	}

	public FormattedString replace(@NotNull String regex, @NotNull String replacement) {
		this.context = Pattern.compile(regex, Pattern.LITERAL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(this.context)
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
		return context;
	}

}
