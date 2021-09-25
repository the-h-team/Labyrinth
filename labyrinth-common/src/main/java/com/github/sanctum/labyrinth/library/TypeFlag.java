package com.github.sanctum.labyrinth.library;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface TypeFlag<T> {

	TypeFlag<Player> PLAYER = () -> Player.class;

	Class<T> getType();

}
