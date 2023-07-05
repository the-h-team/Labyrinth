package com.github.sanctum.labyrinth.gui.basalt;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;

public enum InventorySize {

	/**
	 * Slots: 9
	 */
	ONE(9),

	/**
	 * Slots: 18
	 */
	TWO(18),

	/**
	 * Slots: 27
	 */
	THREE(27),

	/**
	 * Slots: 36
	 */
	FOUR(36),

	/**
	 * Slots: 45
	 */
	FIVE(45),

	/**
	 * Slots: 54
	 */
	SIX(54);

	private final int slots;

	InventorySize(int slots) {
		this.slots = slots;
	}

	/**
	 * @return The size of the inventory.
	 */
	public int getSize() {
		return slots;
	}

	public int[] getSlots(InventoryPane layout) {
		return layout.get(getSize());
	}

}
