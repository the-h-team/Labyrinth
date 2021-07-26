package com.github.sanctum.labyrinth.formatting;

/**
 * Encapsulate quick completion results and feed them live to your command's tab completer.
 *
 * @author [Hempfest, ms5984]
 */
public class TabCompletion {

	/**
	 * Build a list of tab completions using the provided string arguments.
	 *
	 * @param command the command this completion is for
	 * @return a tab completion filtration builder
	 */
	public static TabCompletionBuilder build(String command) {
		return new TabCompletionBuilder().forCommand(command);
	}

}
