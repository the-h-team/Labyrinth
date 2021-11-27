package com.github.sanctum.labyrinth.placeholders;

import java.util.UUID;
import org.bukkit.OfflinePlayer;

@FunctionalInterface
public interface PlaceholderVariable {

	Object get();

	default boolean exists() {
		return get() != null;
	}

	default boolean isPlayer() {
		return get() instanceof OfflinePlayer;
	}

	default boolean isString() {
		return get() instanceof String;
	}

	default boolean isNumber() {
		return get() instanceof Number;
	}

	default boolean isUUID() {
		return get() instanceof UUID;
	}

	default OfflinePlayer getAsPlayer() {
		return (OfflinePlayer) get();
	}

	default UUID getAsUUID() {
		return (UUID) get();
	}

	default String getAsString() {
		return get().toString();
	}

	default Number getAsNumber() {
		return (Number) get();
	}

}
