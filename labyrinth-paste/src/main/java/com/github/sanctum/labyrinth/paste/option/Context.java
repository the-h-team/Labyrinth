package com.github.sanctum.labyrinth.paste.option;

/**
 * An object for providing context to a given adaption of string information.
 */
@FunctionalInterface
public interface Context {

	/**
	 * Plain text.
	 */
	String TEXT = "text";

	/**
	 * java code.
	 */
	String JAVA = "java";

	/**
	 * Json text.
	 */
	String JSON = "json";

	/**
	 * Markdown text.
	 */
	String MARKDOWN = "markdown";


	/**
	 * @return the underlying information tied to this context.
	 */
	String get();

}
