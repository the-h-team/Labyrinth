package com.github.sanctum.labyrinth.gui.unity.construct;

import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.gui.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.gui.unity.impl.PreProcessElement;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SingularMenu extends Menu {

	public SingularMenu(Plugin host, String title, Rows rows, Type type, Property... properties) {
		super(host, title, rows, type, properties);
		if (getProperties().contains(Property.SHAREABLE)) {
			if (!getProperties().contains(Property.ANIMATED)) {
				addElement(new InventoryElement.Shared(title, this));
			} else {
				addElement(new InventoryElement.Normal(title, this));
			}
		} else {
			addElement(new InventoryElement.Normal(title, this));
		}

		TaskScheduler.of(() -> {
			if (getProperties().contains(Property.SAVABLE)) {
				retrieve();
			}
		}).scheduleLater(3);

	}

	@Override
	public InventoryElement.Normal getInventory() {
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
