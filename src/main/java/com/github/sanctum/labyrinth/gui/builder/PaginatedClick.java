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

	private final boolean isLeftClick;

	private final boolean isRightClick;

	private final boolean isShiftClick;

	protected PaginatedClick(PaginatedBuilder builder, Player p, InventoryView view, ItemStack item, boolean isLeft, boolean isRight, boolean isShift) {
		this.builder = builder;
		this.p = p;
		this.view = view;
		this.clickedItem = item;
		this.isLeftClick = isLeft;
		this.isRightClick = isRight;
		this.isShiftClick = isShift;
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
	 * Get the current page.
	 *
	 * @return The page the player is currently viewing.
	 */
	public int getPage() {
		return builder.page;
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
		builder.inv = Bukkit.createInventory(null, builder.size, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
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

	/**
	 * Check if the click was a left mouse button click.
	 *
	 * @return false if the click wasn't a left mouse button.
	 */
	public boolean isLeftClick() {
		return isLeftClick;
	}

	/**
	 * Check if the click was a shift button click.
	 *
	 * @return false if the click wasn't a shift button click.
	 */
	public boolean isShiftClick() {
		return isShiftClick;
	}

	/**
	 * Check if the click was a right mouse button click.
	 *
	 * @return false if the click wasn't a right mouse button.
	 */
	public boolean isRightClick() {
		return isRightClick;
	}
}
