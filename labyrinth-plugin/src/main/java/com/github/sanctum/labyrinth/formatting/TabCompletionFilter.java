package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.library.Applicable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * An object that specifies options for tab-completion.
 *
 * @author [Hempfest, ms5984]
 */
public class TabCompletionFilter {

	protected List<String> completions;
	protected final TabCompletionBuilder text;
	protected final int length;
	protected String key;
	protected int index;
	protected int keyIndex;
	protected boolean anywhere;

	protected TabCompletionFilter(TabCompletionBuilder text, int length) {
		this.text = text;
		this.length = length;
	}

	/**
	 * Require a specific command @ a specific index to be used before offering
	 * the provided completions.
	 *
	 * @param key   The command to require
	 * @param index The string argument index of the command.
	 * @return The same filtered tab completion.
	 */
	public TabCompletionFilter require(String key, int index) {
		this.key = key;
		this.keyIndex = index;
		return this;
	}

	/**
	 * Using this option ignores {@link TabCompletionFilter#completeAt(int)} and all
	 * other options except for {@link TabCompletionFilter#filter(Supplier)}.
	 * <p>
	 * Primarily to be used @ {@link TabCompletionBuilder#level(int)} = 1
	 * for base command to initial sub-command completions.
	 * <p>
	 * Only checks for the specified label at args[0] then feeds completions.
	 *
	 * @return The same filtered tab completion.
	 */
	public TabCompletionFilter completeAt(String commandLabel) {
		this.anywhere = true;
		this.key = commandLabel;
		return this;
	}

	/**
	 * Use this option to specify the index that our completions will be shown/detected
	 * when in live use.
	 *
	 * @param index The string argument index to detect/show completions
	 * @return The same filtered tab completion.
	 */
	public TabCompletionFilter completeAt(int index) {
		this.index = index;
		return this;
	}

	/**
	 * Provide the completions to be injected when in use,
	 * customizing things like permissions and other possible pre-process actions you
	 * want to take.
	 *
	 * @param completions The string completions to provide live
	 * @return The same filtered tab completion.
	 */
	public TabCompletionFilter filter(Supplier<List<String>> completions) {
		this.completions = completions.get();
		return this;
	}

	/**
	 * Map a custom code reference when a specified completion gets triggered.
	 *
	 * @param completion The command to use
	 * @param action     The action to run
	 * @return The same filtered tab completion.
	 */
	public TabCompletionFilter map(String completion, Applicable action) {
		TabCompletionBuilder.APPLICABLE_MAP.putIfAbsent(completion, action);
		return this;
	}

	/**
	 * Save all option's and convert back into a builder to export our list.
	 *
	 * @return Our previous completion builder with our newly configured values.
	 */
	public TabCompletionBuilder collect() {
		List<TabCompletionFilter> indexList;
		if (!TabCompletionBuilder.COMPLETION_MAP.containsKey(length)) {
			indexList = new ArrayList<>();
			indexList.add(this);
		} else {
			indexList = new ArrayList<>(TabCompletionBuilder.COMPLETION_MAP.get(length));
			if (!indexList.contains(this)) {
				indexList.add(this);
			}
		}
		TabCompletionBuilder.COMPLETION_MAP.put(length, indexList);
		return text;
	}

}
