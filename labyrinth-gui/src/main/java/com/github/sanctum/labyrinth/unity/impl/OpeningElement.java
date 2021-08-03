package com.github.sanctum.labyrinth.unity.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class OpeningElement extends PlayerElement {

	private boolean cancelled;

	public OpeningElement(Player clicker, InventoryView view) {
		super(clicker, view);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
