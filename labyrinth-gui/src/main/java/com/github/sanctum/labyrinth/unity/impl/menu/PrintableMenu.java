package com.github.sanctum.labyrinth.unity.impl.menu;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.AnvilMechanics;
import com.github.sanctum.labyrinth.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.unity.impl.inventory.AnvilInventory;
import com.github.sanctum.labyrinth.unity.construct.Menu;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PrintableMenu extends Menu {

	private final AnvilMechanics nms;

	public PrintableMenu(Plugin host, String title, Rows rows, Type type, Property... properties) {
		super(host, title, rows, type, properties);

		AnvilMechanics mechanics = Bukkit.getServicesManager().load(AnvilMechanics.class);

		if (mechanics != null) {
			this.nms = mechanics;
			addElement(new AnvilInventory(title, type, getProperties(), rows));
		} else {
			LabyrinthProvider.getInstance().getLogger().severe("- No anvil mechanic service found!!");
			this.nms = null;
		}

	}

	@Override
	public InventoryElement getInventory() {
		return getElement(e -> e instanceof InventoryElement);
	}
}
