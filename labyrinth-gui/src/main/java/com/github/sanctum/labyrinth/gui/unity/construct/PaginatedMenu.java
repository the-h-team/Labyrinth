package com.github.sanctum.labyrinth.gui.unity.construct;

import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.gui.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.gui.unity.impl.PreProcessElement;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PaginatedMenu extends Menu {

	public PaginatedMenu(Plugin host, String title, Rows rows, Type type, Property... properties) {
		super(host, title, rows, type, properties);
		if (getProperties().contains(Property.SHAREABLE)) {
			if (!getProperties().contains(Property.ANIMATED)) {
				addElement(new InventoryElement.SharedPaginated(title, this));
			} else {
				addElement(new InventoryElement.Paginated(title, this));
			}
		} else {
			addElement(new InventoryElement.Paginated(title, this));
		}

		this.properties.add(Property.REFILLABLE);

		Schedule.sync(() -> {
			if (getProperties().contains(Property.SAVABLE)) {
				retrieve();
			}
		}).waitReal(3);

	}

	@Override
	public InventoryElement.Paginated getInventory() {
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
