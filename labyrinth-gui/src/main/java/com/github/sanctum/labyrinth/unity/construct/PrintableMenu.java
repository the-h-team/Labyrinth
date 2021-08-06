package com.github.sanctum.labyrinth.unity.construct;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.AnvilMechanics;
import com.github.sanctum.labyrinth.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.unity.impl.PreProcessElement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PrintableMenu extends Menu {

	public PrintableMenu(Plugin host, String title, Rows rows, Type type, Property... properties) {
		super(host, title, rows, type, properties);

		AnvilMechanics mechanics = Bukkit.getServicesManager().load(AnvilMechanics.class);
		if (mechanics != null) {
			addElement(new InventoryElement.Printable(title, mechanics, this));
		} else {
			LabyrinthProvider.getInstance().getLogger().severe("- No anvil mechanic service found!!");
			addElement(new InventoryElement.Normal(title, this));
		}

	}

	@Override
	public InventoryElement getInventory() {
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
