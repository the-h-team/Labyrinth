package com.github.sanctum.labyrinth.interfacing;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Identifiable {

	@NotNull String getName();

	@NotNull UUID getId();

	static @NotNull Identifiable wrap(@NotNull Entity entity) {
		return new Identifiable() {
			@Override
			public @NotNull String getName() {
				return entity.getName();
			}

			@Override
			public @NotNull UUID getId() {
				return entity.getUniqueId();
			}
		};
	}

}
