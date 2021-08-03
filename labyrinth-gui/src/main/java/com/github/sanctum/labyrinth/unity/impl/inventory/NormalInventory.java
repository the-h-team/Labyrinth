package com.github.sanctum.labyrinth.unity.impl.inventory;


import com.github.sanctum.labyrinth.unity.construct.Menu;
import com.github.sanctum.labyrinth.unity.impl.InventoryElement;
import java.util.Set;

public class NormalInventory extends InventoryElement {
	public NormalInventory(String title, Menu.Type type, Set<Menu.Property> properties, Menu.Rows rows) {
		super(title, type, properties, rows, true);
	}

}
