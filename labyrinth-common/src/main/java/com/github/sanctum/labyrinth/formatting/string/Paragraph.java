package com.github.sanctum.labyrinth.formatting.string;

/**
 * Encapsulate a string and convert it into paragraphs using a specified regex pattern.
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
	 * <em>Defaults include:</em>
	 * <ul>
	 *     <li>{@link Paragraph#SEPARATE_PERIOD_ONLY}</li>
	 *     <li>{@link Paragraph#SEPARATE_COMMA_ONLY}</li>
	 *     <li>{@link Paragraph#COMMA_AND_PERIOD}</li>
	 * </ul>
	 *
	 * @param regex the pattern regex to match
	 * @return this paragraph object
	 */
	public Paragraph setRegex(String regex) {
		this.regex = regex;
		return this;
	}

	/**
	 * Get the newly-formatted paragraph.
	 *
	 * @return the newly-formatted paragraph
	 */
	public String[] get() {
		return this.text.split(this.regex);
	}

	/**
	 * Get a specific line from the paragraph.
	 *
	 * @param lineNumber the line number
	 * @return the desired paragraph line or the last
	 * element if lineNumber exceeds element count
	 */
	public String get(int lineNumber) {
		return get()[Math.min(lineNumber, get().length)];
	}


}
