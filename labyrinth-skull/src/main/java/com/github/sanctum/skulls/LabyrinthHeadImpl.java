package com.github.sanctum.skulls;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

final class LabyrinthHeadImpl extends CustomHead {

	private final String name;

	private final String category;

	private final ItemStack item;

	LabyrinthHeadImpl(String name, String category, ItemStack item) {
		this.name = name;
		this.category = category;
		this.item = item;
	}

	LabyrinthHeadImpl(String name, String category, ItemStack item, UUID owner) {
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
