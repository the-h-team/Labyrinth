package com.github.sanctum.labyrinth.gui.shared;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class SharedClick {

	private final InventoryClickEvent e;

	protected SharedClick(InventoryClickEvent e) {
		this.e = e;
	}

	/**
	 * Cancel the ability for the player to modify the item.
	 *
	 * @return A shared click.
	 */
	public SharedClick cancel() {
		e.setCancelled(true);
		return this;
	}

	/**
	 * Get the player who clicked on the menu.
	 *
	 * @return The player who clicked.
	 */
	public Player getWhoClicked() {
		return (Player) e.getWhoClicked();
	}


	/**
	 * Get the inventory view of the observing player.
	 *
	 * @return The player's inventory view.
	 */
	public InventoryView getView() {
		return e.getView();
	}

	/**
	 * Get the item clicked on within the menu.
	 *
	 * @return The item clicked on or null.
	 */
	public ItemStack getClickedItem() {
		return e.getCurrentItem();
	}

}
