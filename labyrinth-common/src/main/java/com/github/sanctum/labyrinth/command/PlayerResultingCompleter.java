package com.github.sanctum.labyrinth.command;

import com.github.sanctum.labyrinth.formatting.TabCompletionBuilder;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerResultingCompleter {

	TabCompletionBuilder run(Player player, String[] args, TabCompletionBuilder builder);

}
