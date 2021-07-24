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
 * An event that is triggered upon the instance of a player successfully switching pages in a menu.
 */
public class SyncMenuClickItemEvent<T> extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final ItemStack item;

	private final InventoryView view;

	private final PaginatedBuilder<T> builder;

	private final Player whoClicked;

	private boolean cancelled;

	public SyncMenuClickItemEvent(PaginatedBuilder<T> builder, Player whoClicked, InventoryView view, ItemStack item) {
		this.builder = builder;
		this.whoClicked = whoClicked;
		this.view = view;
		this.item = item;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Get the item that was clicked within the menu.
	 *
	 * @return the item clicked on
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * Get the unique ID linked with the interacted menu.
	 *
	 * @return the unique ID of the clicked menu
	 */
	public UUID getId() {
		return builder.getId();
	}

	/**
	 * Get the player involved in the event.
	 *
	 * @return the player who clicked in the menu
	 */
	public Player getWhoClicked() {
		return whoClicked;
	}

	/**
	 * Get the inventory view involved in the event.
	 *
	 * @return the inventory view of the menu
	 */
	public InventoryView getView() {
		return view;
	}

	/**
	 * Re-open the current page the player is on.
	 */
	public void refresh() {
		builder.inventory = Bukkit.createInventory(null, builder.size, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		whoClicked.openInventory(builder.adjust(builder.page).getInventory());
	}

	/**
	 * Close the menu.
	 */
	public void close() {
		whoClicked.closeInventory();
		HandlerList.unregisterAll(builder.controller);
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
