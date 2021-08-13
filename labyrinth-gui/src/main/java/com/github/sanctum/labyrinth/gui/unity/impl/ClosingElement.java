package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class ClosingElement extends PlayerElement {

	private boolean cancelled;

	private final Menu menu;

	private final Inventory main;

	public ClosingElement(Menu menu, Player clicker, InventoryView view) {
		super(clicker, view);
		this.menu = menu;
		this.main = view.getTopInventory();
	}

	public ClosingElement(Menu menu, Player clicker, Inventory inventory, InventoryView view) {
		super(clicker, view);
		this.menu = menu;
		this.main = inventory;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Inventory getMain() {
		return this.main;
	}

	public Menu getParent() {
		return menu;
	}

	public boolean isCancelled() {
		return cancelled;
	}
}
