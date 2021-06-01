package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.library.Cuboid;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CuboidSelectionEvent extends CuboidSelectEvent {

	private static final HandlerList handlers = new HandlerList();

	public CuboidSelectionEvent(Cuboid.Selection selection) {
		super(selection);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}


}
