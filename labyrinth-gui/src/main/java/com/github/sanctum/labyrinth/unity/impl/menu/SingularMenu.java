package com.github.sanctum.labyrinth.unity.impl.menu;

import com.github.sanctum.labyrinth.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.unity.impl.inventory.NormalInventory;
import com.github.sanctum.labyrinth.unity.construct.Menu;
import com.github.sanctum.labyrinth.unity.impl.inventory.SharedInventory;
import org.bukkit.plugin.Plugin;

public class SingularMenu extends Menu {

	public SingularMenu(Plugin host, String title, Rows rows, Type type, Property... properties) {
		super(host, title, rows, type, properties);
		if (getProperties().contains(Property.SHAREABLE)) {
			if (!getProperties().contains(Property.ANIMATED)) {
				addElement(new SharedInventory(title, type, getProperties(), rows));
			} else {
				addElement(new NormalInventory(title, type, getProperties(), rows));
			}
		} else {
			addElement(new NormalInventory(title, type, getProperties(), rows));
		}

		if (getProperties().contains(Property.SAVABLE)) {
			retrieve();
		}

	}

	@Override
	public InventoryElement getInventory() {
		return getElement(e -> e instanceof InventoryElement);
	}



}
