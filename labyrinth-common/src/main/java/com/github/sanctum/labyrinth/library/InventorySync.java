package com.github.sanctum.labyrinth.library;

import com.github.sanctum.panther.util.Deployable;
import org.bukkit.inventory.Inventory;

/**
 * An object used to envelope inventory contents for proper removal.
 */
public abstract class InventorySync implements CompostElement {

	private final Inventory parent;
	private final int amount;

	public InventorySync(Inventory inventory) {
		this.parent = inventory;
		this.amount = -1;
	}

	public InventorySync(Inventory inventory, int amount) {
		this.parent = inventory;
		this.amount = amount;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public Inventory getParent() {
		return parent;
	}

	@Override
	public Deployable<Void> remove(ItemCompost compost) {
		return Deployable.of(null, unused -> compost.remove(this), 0);
	}

}
