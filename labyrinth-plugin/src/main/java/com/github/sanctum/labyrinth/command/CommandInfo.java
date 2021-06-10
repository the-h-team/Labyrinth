package com.github.sanctum.labyrinth.command;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CommandInfo {

	private final CommandBuilder builder;

	protected CommandInfo(CommandBuilder builder) {
		this.builder = builder;
	}

	public @NotNull Plugin getPlugin() {
		return builder.plugin;
	}

	public @NotNull String getLabel() {
		return builder.label;
	}

	public @Nullable String getDescription() {
		return builder.description;
	}

	public @Nullable String getPermission() {
		return builder.permission;
	}

	public @NotNull String getUsage() {
		return builder.usage;
	}

}
