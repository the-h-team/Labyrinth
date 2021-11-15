package com.github.sanctum.labyrinth.paste.operative;

import com.github.sanctum.labyrinth.library.ListUtils;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.paste.option.Context;

/**
 * A child component of {@link Context} for retaining information retrieved from a web connection.
 */
@FunctionalInterface
public interface PasteResponse extends Context {

	/**
	 * @return all responses from this web interaction.
	 */
	default String[] getAll() {
		return get().split("");
	}

	/**
	 * @return true if this response is valid.
	 */
	default boolean isValid() {
		return get() != null && !get().equals("NA");
	}

	/**
	 * @return true if this response is just a domain address.
	 */
	default boolean isLink() {
		return get().startsWith("http");
	}

	/**
	 * @return true if this response contains a link.
	 */
	default boolean containsLink() {
		return StringUtils.use(get()).containsIgnoreCase("http", "https") || ListUtils.use(getAll()).stringContainsIgnoreCase("http", "https");
	}

}
