package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class OpeningElement extends PlayerElement {

	private boolean cancelled;

	private final Menu menu;

	public OpeningElement(Menu menu, Player clicker, InventoryView view) {
		super(clicker, view);
		this.menu = menu;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public Menu getParent() {
		return menu;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
