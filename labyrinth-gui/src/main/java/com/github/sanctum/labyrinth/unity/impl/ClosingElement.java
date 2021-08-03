package com.github.sanctum.labyrinth.unity.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class ClosingElement extends PlayerElement {

	private boolean cancelled;

	public ClosingElement(Player clicker, InventoryView view) {
		super(clicker, view);
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCancelled() {
		return cancelled;
	}
}
