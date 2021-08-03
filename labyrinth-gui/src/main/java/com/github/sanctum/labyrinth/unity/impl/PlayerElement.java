package com.github.sanctum.labyrinth.unity.impl;

import com.github.sanctum.labyrinth.unity.construct.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public abstract class PlayerElement extends Menu.Element<Player, InventoryView> {

	private final Player clicker;
	private final InventoryView view;

	public PlayerElement(Player clicker, InventoryView view) {
		this.clicker = clicker;
		this.view = view;
	}

	@Override
	public Player getElement() {
		return this.clicker;
	}

	@Override
	public InventoryView getAttachment() {
		return this.view;
	}

}
