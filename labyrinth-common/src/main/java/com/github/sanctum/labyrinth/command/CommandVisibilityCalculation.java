package com.github.sanctum.labyrinth.command;

import java.util.function.Function;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandVisibilityCalculation extends Function<Player, Boolean> {

	Boolean apply(@NotNull Player player);

}
