package com.github.sanctum.labyrinth.gui.builder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * An operation that decides what happens when an item is clicked on.
 */
public class PaginatedClick {

	private final InventoryView view;

	private final Player p;

	private final ItemStack clickedItem;

	private final PaginatedBuilder builder;

	protected PaginatedClick(PaginatedBuilder builder, Player p, InventoryView view, ItemStack item) {
		this.builder = builder;
		this.p = p;
		this.view = view;
		this.clickedItem = item;
	}

	/**
	 * Get the item clicked on.
	 *
	 * @return An itemstack from the operation.
	 */
	public ItemStack getClickedItem() {
		return clickedItem;
	}

	/**
	 * Get the player.
	 *
	 * @return The player involved in the operation.
	 */
	public Player getPlayer() {
		return p;
	}

	/**
	 * *RECOMMENDED*: Used to update the items displayed.
	 */
	public void sync() {
		builder.inv = Bukkit.createInventory(null, 54, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust().getInventory());
	}

	/**
	 * Get the inventory view from the operation.
	 *
	 * @return The inventory view from the operation.
	 */
	public InventoryView getView() {
		return view;
	}
}
