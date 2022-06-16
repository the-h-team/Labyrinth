package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.annotation.See;
import com.github.sanctum.labyrinth.data.MemorySpace;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.interfacing.JsonIntermediate;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * A helpful delegation interface for fluid labyrinth unity menu creation using various methods of retrieval.
 *
 * @param <T> The optional type used for pagination
 */
@See({DocketBuilder.class, JsonDocket.class, MapDocket.class, MemoryDocket.class})
public interface Docket<T> extends JsonIntermediate {

	@Note("A useful variable processing step")
	default @NotNull Docket<T> load() {
		return this;
	}

	/**
	 * Get the menu this docket creates.
	 *
	 * @return The translated menu.
	 */
	@NotNull Menu toMenu();

	/**
	 * Get the type of this docket.
	 *
	 * @return This docket type.
	 */
	@NotNull Type getType();

	enum Type {
		/**
		 * Defines a docket type that requires being built.
		 */
		BUILDER,
		/**
		 * Defines a docket type that is derived from a type of memory space.
		 */
		MEMORY,
		/**
		 * Defines a docket type that is completely custom.
		 */
		CUSTOM
	}

	/**
	 * Create a fresh docket instance using a map object.
	 *
	 * @param map The map to use in translation.
	 * @param <V> The optional type of docket for pagination.
	 * @return A fresh docket instance.
	 */
	static <V> @NotNull MapDocket<V> newInstance(@NotNull Map<String, Object> map) {
		return new MapDocket<>(map);
	}

	/**
	 * Create a fresh docket instance using a {@link MemorySpace} object.
	 *
	 * @param memorySpace The memory space to use in translation.
	 * @param <V> The optional type of docket for pagination.
	 * @return A fresh docket instance.
	 */
	static <V> @NotNull MemoryDocket<V> newInstance(@NotNull MemorySpace memorySpace) {
		return new MemoryDocket<>(memorySpace);
	}

	/**
	 * Create a fresh docket instance using a json object.
	 *
	 * @param json The json object to use in translation.
	 * @param <V> The optional type of docket for pagination.
	 * @return A fresh docket instance.
	 */
	static <V> @NotNull JsonDocket<V> newInstance(@NotNull @Json String json) {
		return new JsonDocket<>(json);
	}

	/**
	 * Conform the provided docket into a processed menu.
	 *
	 * @param docket The docket to use.
	 * @return A translated labyrinth unity menu.
	 */
	static Menu toMenu(@NotNull Docket<?> docket) {
		return docket.getType() == Type.MEMORY ? docket.load().toMenu() : docket.toMenu();
	}

}
