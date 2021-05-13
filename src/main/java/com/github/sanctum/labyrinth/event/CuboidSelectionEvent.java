package com.github.sanctum.labyrinth.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CuboidSelectionEvent extends Event {

	private static final HandlerList handlers = new HandlerList();


	public CuboidSelectionEvent() {

	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}


}
