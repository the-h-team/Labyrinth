package com.github.sanctum.labyrinth.formatting;

/**
 * Encapsulate quick completion results and feed them live to your command's tab completer.
 */
public class TabCompletion {

	/**
	 * Build a list of tab completions using the provided string arguments.
	 *
	 * @param command The command the completion building is for.
	 * @return A tab completion filtration builder.
	 */
	public static TabCompletionBuilder build(String command) {
		return new TabCompletionBuilder().forCommand(command);
	}

}
