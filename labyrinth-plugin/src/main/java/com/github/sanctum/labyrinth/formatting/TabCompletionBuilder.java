package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.library.Applicable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * An object that encapsulates tab completion data for live injection.
 *
 * @author [Hempfest, ms5984]
 */
public class TabCompletionBuilder {

	protected String[] args;
	protected String commandLabel;
	protected static Map<Integer, List<TabCompletionFilter>> COMPLETION_MAP = new HashMap<>();
	protected static Map<String, Applicable> APPLICABLE_MAP = new HashMap<>();

	protected TabCompletionBuilder() {
	}

	/**
	 * Supply the string arguments for completion.
	 *
	 * @param args The string arguments to provide.
	 * @return The same completion builder.
	 */
	public TabCompletionBuilder forArgs(String[] args) {
		this.args = args;
		return this;
	}

	/**
	 * Configure completion results & filter by string argument length
	 * <p>
	 * Basic accordance should be applied here from tab complete method.
	 * Example: ( args[int].length() )
	 *
	 * @param length The string argument length to customize completions for.
	 * @return A tab completion filter
	 */
	public TabCompletionFilter level(int length) {
		return new TabCompletionFilter(this, length);
	}

	/**
	 * For internal use. Specify the command needed for anywhere completion.
	 *
	 * @param label The command to check for.
	 * @return The same builder.
	 */
	protected TabCompletionBuilder forCommand(String label) {
		this.commandLabel = label;
		return this;
	}

	/**
	 * Get the filtered completions from the builder.
	 *
	 * @param length The string argument length to pull completions for.
	 * @return The provided list of completions based off of filter results, if no filter results are
	 * present then an empty list will be provided for completion.
	 */
	public @NotNull List<String> get(int length) {
		List<TabCompletionFilter> indexs = COMPLETION_MAP.getOrDefault(length, null);
		List<String> results = new ArrayList<>();
		if (indexs == null) {
			return results;
		}
		for (TabCompletionFilter index : indexs) {
			if (!index.anywhere) {
				if (args.length == index.length && Arrays.stream(args).anyMatch(s -> index.key.equalsIgnoreCase(s))) {
					if (args[index.keyIndex].equalsIgnoreCase(index.key)) {
						for (String completion : index.completions) {
							if (completion.toLowerCase().startsWith(args[index.index].toLowerCase())) {
								if (APPLICABLE_MAP.get(completion) != null) {
									APPLICABLE_MAP.get(completion).apply();
									APPLICABLE_MAP.remove(completion);
								}
								results.add(completion);
							}
						}
					}
				}
			} else {
				if (args.length == index.length) {
					if (index.key.equals(commandLabel)) {
						for (String completion : index.completions) {
							if (completion.toLowerCase().startsWith(args[Math.max(args.length - 1, 0)].toLowerCase())) {
								if (APPLICABLE_MAP.get(completion) != null) {
									APPLICABLE_MAP.get(completion).apply();
									APPLICABLE_MAP.remove(completion);
								}
								results.add(completion);
							}
						}
					}
				}
			}
		}
		return results;
	}


}
