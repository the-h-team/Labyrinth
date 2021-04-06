package com.github.sanctum.labyrinth.gui.builder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * An operation that decides what happens when a menu is closed.
 */
public class PaginatedClose {

	private final InventoryView view;

	private final Player p;

	private final PaginatedBuilder builder;

	protected PaginatedClose(PaginatedBuilder builder, Player p, InventoryView view) {
		this.builder = builder;
		this.p = p;
		this.view = view;
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
	 * Clear cache, remove un-used handlers.
	 */
	public void clear() {
		InventoryClickEvent.getHandlerList().unregister(builder.getListener());
		InventoryCloseEvent.getHandlerList().unregister(builder.getListener());
		SyncMenuItemPreProcessEvent.getHandlerList().unregister(builder.getListener());
	}

	/**
	 * Cancel the inventory close >:)
	 *
	 * Re-opens the GUI back to the same page the player was in.
	 */
	public void cancel() {
		builder.inv = Bukkit.createInventory(null, builder.size, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust(builder.page).getInventory());
	}

	/**
	 * Get the inventory view involved in the operation.
	 *
	 * @return The inventory view involved in the operation.
	 */
	public InventoryView getView() {
		return view;
	}
}
