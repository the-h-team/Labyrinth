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
	 * @return an ItemStack from the operation
	 */
	public ItemStack getClickedItem() { // TODO: assess nullity
		return clickedItem;
	}

	/**
	 * Get the page to be navigated to next whether forward of backward.
	 *
	 * @return the page the player is going next
	 */
	public int getPage() {
		return builder.page;
	}

	/**
	 * Get the player.
	 *
	 * @return the player involved in the operation
	 */
	public Player getPlayer() {
		return p;
	}

	/**
	 * If your inventory isn't live updating use this to switch pages when players click the navigation buttons.
	 */
	public void sync() {
		builder.inventory = Bukkit.createInventory(null, builder.size, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust().getInventory());
	}

	/**
	 * If your inventory isn't live updating use this to switch pages when players click the navigation buttons.
	 */
	public void sync(int page) {
		builder.inventory = Bukkit.createInventory(null, builder.size, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust(page).getInventory());
	}

	public void sync(int delay, int period) {
		this.builder.inventory = Bukkit.createInventory(null, this.builder.size, this.builder.title.replace("{PAGE}", "" + (this.builder.page + 1)).replace("{MAX}", "" + this.builder.getMaxPages()));

		if (this.builder.live) {
			this.builder.inventory.setMaxStackSize(1);
			if (this.builder.task.containsKey(p)) {
				this.builder.task.get(p).cancelTask();
			}

			this.builder.task.put(p, Schedule.async(() -> Schedule.sync(() -> {
				this.builder.inventory.clear();
				this.builder.adjust();
			}).run()));
			this.builder.task.get(p).repeat(delay, period);
			Schedule.sync(() -> p.openInventory(this.builder.getInventory())).waitReal(delay + 1);
		} else {
			p.openInventory(this.builder.adjust().getInventory());
		}
	}

	public void sync(int page, int delay, int period) {
		this.builder.inventory = Bukkit.createInventory(null, this.builder.size, this.builder.title.replace("{PAGE}", "" + (this.builder.page + 1)).replace("{MAX}", "" + this.builder.getMaxPages()));

		if (this.builder.live) {
			this.builder.inventory.setMaxStackSize(1);
			if (this.builder.task.containsKey(p)) {
				this.builder.task.get(p).cancelTask();
			}

			this.builder.task.put(p, Schedule.async(() -> Schedule.sync(() -> {
				this.builder.inventory.clear();
				this.builder.adjust(page);
			}).run()));
			this.builder.task.get(p).repeat(delay, period);
			Schedule.sync(() -> p.openInventory(this.builder.getInventory())).waitReal(delay + 1);
		} else {
			p.openInventory(this.builder.adjust().getInventory());
		}
	}

	/**
	 * Get the inventory view from the operation.
	 *
	 * @return the inventory view from the operation
	 */
	public InventoryView getView() {
		return view;
	}

	/**
	 * Check if the click was a left mouse button click.
	 *
	 * @return false if the click was not a left mouse button
	 */
	public boolean isLeftClick() {
		return isLeftClick;
	}

	/**
	 * Check if the click was a shift button click.
	 *
	 * @return false if the click was not a shift button click
	 */
	public boolean isShiftClick() {
		return isShiftClick;
	}

	/**
	 * Check if the click was with middle mouse button.
	 *
	 * @return false if the click was not a middle mouse button click
	 */
	public boolean isMiddleClick() {
		return isMiddleClick;
	}

	/**
	 * Check if the click was a right mouse button click.
	 *
	 * @return false if the click was not a right mouse button
	 */
	public boolean isRightClick() {
		return isRightClick;
	}

}
