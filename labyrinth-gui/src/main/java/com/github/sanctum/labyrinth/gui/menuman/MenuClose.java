/*
 *  Copyright 2021 ms5984 (Matt) <https://github.com/ms5984>
 *
 *  This file is part of MenuMan, a module of Labyrinth.
 *
 *  MenuMan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  MenuMan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.sanctum.labyrinth.gui.menuman;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

/**
 * Class which encapsulates relevant data of InventoryCloseEvent
 */
public final class MenuClose {
	/**
	 * The player closing the Menu.
	 */
	protected final Player player;
	private final InventoryCloseEvent inventoryCloseEvent;

	/**
	 * Create a {@link MenuClose} to pass to a {@link CloseAction}.
	 *
	 * @param e      original event
	 * @param player a player
	 */
	protected MenuClose(InventoryCloseEvent e, Player player) {
		this.player = player;
		inventoryCloseEvent = e;
	}

	/**
	 * Get the player that clicked the Menu.
	 *
	 * @return player
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Get the Menu inventory that is closing.
	 *
	 * @return Menu inventory
	 */
	public Inventory getUpperInventory() {
		return inventoryCloseEvent.getInventory();
	}

	/**
	 * Get the full InventoryView of the transaction.
	 *
	 * @return the InventoryView for the transaction
	 */
	public InventoryView getInventoryView() {
		return inventoryCloseEvent.getView();
	}
}
