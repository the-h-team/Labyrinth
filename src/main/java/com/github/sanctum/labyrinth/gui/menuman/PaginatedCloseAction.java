package com.github.sanctum.labyrinth.gui.menuman;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

/**
 * An operation that decides what happens when a menu is closed.
 */
public class PaginatedCloseAction<T> {

	private final InventoryView view;

	private final Player p;

	private final PaginatedBuilder<T> builder;

	protected PaginatedCloseAction(PaginatedBuilder<T> builder, Player p, InventoryView view) {
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
		InventoryClickEvent.getHandlerList().unregister(builder.getController());
		InventoryCloseEvent.getHandlerList().unregister(builder.getController());
	}

	/**
	 * Cancel the inventory close >:)
	 *
	 * Re-opens the GUI back to the same page the player was in.
	 */
	public void cancel() {
		builder.INVENTORY = Bukkit.createInventory(null, builder.SIZE, builder.TITLE.replace("{PAGE}", "" + (builder.PAGE + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust(builder.PAGE).getInventory());
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
