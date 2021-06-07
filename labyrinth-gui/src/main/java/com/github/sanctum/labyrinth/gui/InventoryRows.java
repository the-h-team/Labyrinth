package com.github.sanctum.labyrinth.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.InventoryHolder;

/**
 * Define inventory size.
 * <p>
 * Helps enforce slot parameter contract of
 * {@link Bukkit#createInventory(InventoryHolder, int, String)}
 * (int must be divisible by 9)
 */
public enum InventoryRows {
	ONE(9),
	TWO(18),
	THREE(27),
	FOUR(36),
	FIVE(45),
	SIX(54);

	/**
	 * Number of slots in an Inventory of these rows.
	 */
	public final int slotCount;

	InventoryRows(int slots) {
		this.slotCount = slots;
	}

	public int getSlotCount() {
		return slotCount;
	}

}