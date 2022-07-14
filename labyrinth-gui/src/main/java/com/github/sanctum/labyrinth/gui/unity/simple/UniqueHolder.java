package com.github.sanctum.labyrinth.gui.unity.simple;

import java.util.function.BiFunction;
import org.jetbrains.annotations.NotNull;

/**
 * An extension interface for {@link Docket}, use to signify this as a unique data dependent.
 */
public interface UniqueHolder {

	<V> Docket<?> setUniqueDataConverter(@NotNull V data, @NotNull BiFunction<String, V, String> function);

	default Docket<?> setNamePlaceholder(@NotNull String placeholder) {
		return null;
	}

}
