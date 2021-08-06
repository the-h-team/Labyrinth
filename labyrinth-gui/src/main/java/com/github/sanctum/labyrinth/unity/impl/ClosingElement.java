package com.github.sanctum.labyrinth.unity.impl;

import com.github.sanctum.labyrinth.unity.construct.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class ClosingElement extends PlayerElement {

	private boolean cancelled;

	private final Menu menu;

	public ClosingElement(Menu menu, Player clicker, InventoryView view) {
		super(clicker, view);
		this.menu = menu;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Menu getParent() {
		return menu;
	}

	public boolean isCancelled() {
		return cancelled;
	}
}