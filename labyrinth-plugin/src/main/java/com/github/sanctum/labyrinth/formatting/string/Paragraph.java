package com.github.sanctum.labyrinth.formatting.string;

/**
 * Encapsulate a string and convert it into paragraph's using a specified regex pattern.
 *
 * @author Hempfest
 */
public class Paragraph {

	public static final String SEPARATE_COMMA_ONLY = ",\\s*";
	public static final String SEPARATE_PERIOD_ONLY = "\\.\\s*";
	public static final String COMMA_AND_PERIOD = ",\\s*|\\.\\s*";

	private String regex = COMMA_AND_PERIOD;
	private final String text;


	public Paragraph(String text) {
		this.text = text;
	}

	/**
	 * Apply a regex to this paragraph sequence.
	 * <p>
	 * Defaults include: [ {@link Paragraph#SEPARATE_PERIOD_ONLY}, {@link Paragraph#SEPARATE_COMMA_ONLY} & {@link Paragraph#COMMA_AND_PERIOD} ]
	 *
	 * @param regex The pattern regex to match.
	 * @return The same paragraph object.
	 */
	public Paragraph setRegex(String regex) {
		this.regex = regex;
		return this;
	}

	/**
	 * Gets the newly formatted paragraph.
	 *
	 * @return Get's the newly made paragraph.
	 */
	public String[] get() {
		return this.text.split(this.regex);
	}

	/**
	 * Get a specific line from the paragraph.
	 *
	 * @param line The line to grab.
	 * @return The desired paragraph line or the last element if the page goes over max.
	 */
	public String get(int line) {
		return get()[Math.min(line, get().length)];
	}


}
