package com.github.sanctum.labyrinth.event;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ItemRecipeProcessEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Material type;

	private final String name;

	public ItemRecipeProcessEvent(Material appearance, String name) {
		this.type = appearance;
		this.name = name;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public String getName() {
		return name;
	}

	public Material getType() {
		return type;
	}
}
