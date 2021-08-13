package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class PreProcessElement extends PlayerElement {

	private boolean cancelled;

	private final Menu menu;

	public PreProcessElement(Menu menu, Player clicker, InventoryView view) {
		super(clicker, view);
		this.menu = menu;
	}

	public Menu getParent() {
		return menu;
	}

}
