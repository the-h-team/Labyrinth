package com.github.sanctum.labyrinth.unity.impl.menu;

import com.github.sanctum.labyrinth.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.unity.impl.inventory.NormalInventory;
import com.github.sanctum.labyrinth.unity.construct.Menu;
import com.github.sanctum.labyrinth.unity.impl.inventory.SharedInventory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PaginatedMenu extends Menu {

	public PaginatedMenu(Plugin host, String title, Rows rows, Type type, Property... properties) {
		super(host, title, rows, type, properties);
		if (getProperties().contains(Property.SHAREABLE)) {
			if (!getProperties().contains(Property.ANIMATED)) {
				addElement(new SharedInventory(title, this));
			} else {
				addElement(new NormalInventory(title, this));
			}
		} else {
			addElement(new NormalInventory(title, this));
		}

		if (getProperties().contains(Property.SAVABLE)) {
			retrieve();
		}

	}

	@Override
	public InventoryElement getInventory() {
		return getElement(e -> e instanceof InventoryElement);
	}

	@Override
	public void open(Player player) {
		getInventory().open(player);
	}

}
