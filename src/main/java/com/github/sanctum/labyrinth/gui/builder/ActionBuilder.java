package com.github.sanctum.labyrinth.gui.builder;

import org.bukkit.inventory.ItemStack;

/**
 * An object designed to provide custom functional interception when an item from the specified collection is clicked.
 */
public class ActionBuilder {

	private final PaginatedBuilder builder;

	private final ItemStack item;

	protected ActionBuilder(ItemStack item, PaginatedBuilder builder) {
		this.builder = builder;
		this.item = item;
	}

	/**
	 * Apply what happens when the item is clicked on.
	 * All data provided will be applied on RUNTIME
	 * @param click The lambda/method reference to apply
	 * @return An action builder.
	 */
	public ActionBuilder setClick(InventoryClick click) {
		builder.actions.putIfAbsent(item, click);
		return this;
	}

	/**
	 * The item involved in the click action.
	 * @return An itemstack.
	 */
	public ItemStack getItem() {
		return item;
	}
}
