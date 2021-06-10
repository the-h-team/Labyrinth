package com.github.sanctum.labyrinth.command;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerResultingExecutor {

	boolean run(Player player, String commandLabel, String[] args);

}
