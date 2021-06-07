package com.github.sanctum.labyrinth.gui.printer;

import org.bukkit.entity.Player;

/**
 * A functional interface providing RUNTIME operations in the event of a menu click
 */
@FunctionalInterface
public interface AnvilItemClick {

	void execute(Player player, String text, String[] args);

}
