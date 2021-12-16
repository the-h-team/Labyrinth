package com.github.sanctum.labyrinth.command;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface CommandVisibilityCalculation {

	String accept(Player player);

}
