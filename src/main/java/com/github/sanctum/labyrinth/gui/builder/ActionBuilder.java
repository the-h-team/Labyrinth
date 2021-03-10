package com.github.sanctum.labyrinth.gui.builder;

import org.bukkit.inventory.ItemStack;

public class ActionBuilder {

	private final PaginatedBuilder builder;

	private final ItemStack item;

	protected ActionBuilder(ItemStack item, PaginatedBuilder builder) {
		this.builder = builder;
		this.item = item;
	}

	public ActionBuilder setClick(InventoryClick click) {
		builder.actions.putIfAbsent(item, click);
		return this;
	}

	public ItemStack getItem() {
		return item;
	}
}
