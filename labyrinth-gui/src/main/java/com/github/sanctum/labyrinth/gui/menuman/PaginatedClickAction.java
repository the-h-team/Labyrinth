package com.github.sanctum.labyrinth.gui.menuman;

import com.github.sanctum.labyrinth.task.Schedule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * An operation that decides what happens when an item is clicked on.
 */
public class PaginatedClickAction<T> {

	private final InventoryView view;

	private final Player p;

	private final ItemStack clickedItem;

	private final PaginatedBuilder<T> builder;

	private final boolean isLeftClick;

	private final boolean isRightClick;

	private final boolean isShiftClick;

	private final boolean isMiddleClick;

	protected PaginatedClickAction(PaginatedBuilder<T> builder, Player p, InventoryView view, ItemStack item, boolean isLeft, boolean isRight, boolean isShift, boolean isMiddle) {
		this.builder = builder;
		this.p = p;
		this.view = view;
		this.clickedItem = item;
		this.isLeftClick = isLeft;
		this.isMiddleClick = isMiddle;
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
		return builder.PAGE;
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
		builder.INVENTORY = Bukkit.createInventory(null, builder.SIZE, builder.TITLE.replace("{PAGE}", "" + (builder.PAGE + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust().getInventory());
	}

	public void sync(int delay, int period) {
		this.builder.INVENTORY = Bukkit.createInventory(null, this.builder.SIZE, this.builder.TITLE.replace("{PAGE}", "" + (this.builder.PAGE + 1)).replace("{MAX}", "" + this.builder.getMaxPages()));

		if (this.builder.LIVE) {
			this.builder.INVENTORY.setMaxStackSize(1);
			if (this.builder.TASK.containsKey(p)) {
				this.builder.TASK.get(p).cancelTask();
			}

			this.builder.TASK.put(p, Schedule.async(() -> Schedule.sync(() -> {
				this.builder.INVENTORY.clear();
				this.builder.adjust();
			}).run()));
			this.builder.TASK.get(p).repeat(delay, period);
			Schedule.sync(() -> p.openInventory(this.builder.getInventory())).waitReal(delay + 1);
		} else {
			p.openInventory(this.builder.adjust().getInventory());
		}
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
	 * Check if the click was with middle mouse button.
	 *
	 * @return false if the click wasnt a middle mouse button click.
	 */
	public boolean isMiddleClick() {
		return isMiddleClick;
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
