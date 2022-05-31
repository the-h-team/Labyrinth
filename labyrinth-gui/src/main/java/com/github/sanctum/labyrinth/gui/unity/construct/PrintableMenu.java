package com.github.sanctum.labyrinth.gui.unity.construct;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.AnvilMechanics;
import com.github.sanctum.labyrinth.gui.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.gui.unity.impl.PreProcessElement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PrintableMenu extends Menu {

	public PrintableMenu(Plugin host, String title, Rows rows, Type type, Property... properties) {
		super(host, title, rows, type, properties);
		this.properties.add(Property.SHAREABLE);
		AnvilMechanics mechanics = Bukkit.getServicesManager().load(AnvilMechanics.class);
		if (mechanics != null) {
			addElement(new InventoryElement.Printable(title, mechanics, this));
		} else {
			LabyrinthProvider.getInstance().getLogger().severe("- No anvil mechanic service found!!");
			addElement(new InventoryElement.Printable(title, null, this));
		}

	}

	@Override
	public InventoryElement.Printable getInventory() {
		return getElement(e -> e instanceof InventoryElement);
	}


	@Override
	public void open(Player player) {
		if (this.process != null) {
			PreProcessElement element = new PreProcessElement(this, player, player.getOpenInventory());
			this.process.apply(element);
		}
		getInventory().open(player);
	}

}
