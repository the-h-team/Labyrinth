package com.github.sanctum.labyrinth.gui.menuman;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is triggered in the instance of a player switching pages within a menu.
 */
public class SyncMenuSwitchPageEvent<T> extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final ItemStack item;

	private final InventoryView view;

	private final PaginatedBuilder<T> builder;

	private final int page;

	private final Player whoClicked;

	private boolean cancelled;

	public SyncMenuSwitchPageEvent(PaginatedBuilder<T> builder, Player whoClicked, InventoryView view, ItemStack item, int page) {
		this.builder = builder;
		this.whoClicked = whoClicked;
		this.view = view;
		this.item = item;
		this.page = page;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * The item that was clicked on.
	 *
	 * @return An itemstack involved within the operation.
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * The unique ID of the menu clicked.
	 *
	 * @return The unique ID of the menu.
	 */
	public UUID getId() {
		return builder.getId();
	}

	/**
	 * The player who clicked.
	 *
	 * @return The player who interacted within the event.
	 */
	public Player getWhoClicked() {
		return whoClicked;
	}

	/**
	 * Gets the inventory view within the operation.
	 *
	 * @return The inventory view for the menu.
	 */
	public InventoryView getView() {
		return view;
	}

	/**
	 * Gets the current page the player is on within the menu.
	 *
	 * @return The page the player is on.
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Re-open the menu on a specified page.
	 *
	 * @param page The page to open for the player.
	 */
	public void open(int page) {
		builder.INVENTORY = Bukkit.createInventory(null, builder.SIZE, builder.TITLE.replace("{PAGE}", "" + (builder.PAGE + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		whoClicked.openInventory(builder.adjust(page).getInventory());
	}

	/**
	 * Re-open the menu page the player is currently on.
	 */
	public void refresh() {
		builder.INVENTORY = Bukkit.createInventory(null, builder.SIZE, builder.TITLE.replace("{PAGE}", "" + (builder.PAGE + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		whoClicked.openInventory(builder.adjust(page).getInventory());
	}

	/**
	 * Close the menu.
	 */
	public void close() {
		whoClicked.closeInventory();
		HandlerList.unregisterAll(builder.CONTROLLER);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
