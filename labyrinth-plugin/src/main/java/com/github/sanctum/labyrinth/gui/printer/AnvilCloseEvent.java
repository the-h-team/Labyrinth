package com.github.sanctum.labyrinth.gui.printer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

/**
 * A functional interface providing RUNTIME operations in the event of a menu closing
 */
@FunctionalInterface
public interface AnvilCloseEvent {

	void execute(Player player, InventoryView view, AnvilMenu menu);

}
