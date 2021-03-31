package com.github.sanctum.labyrinth.formatting;

/**
 * Encapsulate quick completion results and feed them live to your command's tab completer.
 */
public class TabCompletion {

	/**
	 * Build a list of tab completions using the provided string arguments.
	 *
	 * @param args The string arguments to provide
	 * @return A tab completion filtration builder.
	 */
	public static TabCompletionBuilder build(String[] args) {
		return new TabCompletionBuilder(args);
	}

}
