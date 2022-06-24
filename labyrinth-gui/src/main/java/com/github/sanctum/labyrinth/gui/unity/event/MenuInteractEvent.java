package com.github.sanctum.labyrinth.gui.unity.event;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class MenuInteractEvent extends MenuEvent {
	final Type type;
	final Player player;

	public MenuInteractEvent(@NotNull Type type, @NotNull Player player, @NotNull Menu menu) {
		super(menu);
		this.player = player;
		this.type = type;
	}

	public @NotNull Player getPlayer() {
		return player;
	}

	public @NotNull Type getType() {
		return type;
	}

	public enum Type {
		CLICK, DRAG
	}

}
