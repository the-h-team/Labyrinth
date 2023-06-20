package com.github.sanctum.skulls;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
final class DefaultHead extends CustomHead {

	private final String name;

	private final String category;

	private final ItemStack item;

	DefaultHead(@NotNull String name, @NotNull String category, @NotNull ItemStack item) {
		this.name = name;
		this.category = category;
		this.item = item;
	}

	DefaultHead(@NotNull String name, @NotNull  String category, @NotNull ItemStack item, @NotNull UUID owner) {
		super(owner);
		this.category = category;
		this.name = name;
		this.item = item;
	}

	@Override
	public @NotNull ItemStack get() {
		return this.item;
	}

	@Override
	public @NotNull String name() {
		return this.name;
	}

	@Override
	public @NotNull String category() {
		return this.category;
	}
}
