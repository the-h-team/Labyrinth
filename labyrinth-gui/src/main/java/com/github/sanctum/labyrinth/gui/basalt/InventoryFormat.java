package com.github.sanctum.labyrinth.gui.basalt;

import org.bukkit.event.inventory.InventoryType;

public enum InventoryFormat {

	ANVIL,
	NORMAL,
	PAGINATED;


	public InventoryType toBukkit() {
		if (this == ANVIL) return InventoryType.ANVIL;
		return InventoryType.CHEST;
	}

}
