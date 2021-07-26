package com.github.sanctum.labyrinth.command;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface ConsoleResultingExecutor {

	boolean run(CommandSender sender, String commandLabel, String[] args);

}
