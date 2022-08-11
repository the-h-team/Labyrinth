package com.github.sanctum.labyrinth.library;

import com.github.sanctum.panther.util.Deployable;
import org.bukkit.inventory.Inventory;

public interface CompostElement {

	/**
	 * @return The amount to be removed from the sourced inventory.
	 */
	int getAmount();

	/**
	 * @return The sourced inventory.
	 */
	Inventory getParent();

	/**
	 * Remove all corresponding items from the sourced inventory using a provided item composter.
	 *
	 * @param compost The item composter.
	 * @return A deployable item removal procedure.
	 */
	Deployable<Void> remove(ItemCompost compost);

}
