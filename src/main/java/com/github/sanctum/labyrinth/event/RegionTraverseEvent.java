package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionTraverseEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Region.Resident resident;

	public RegionTraverseEvent(Region.Resident resident) {
		this.resident = resident;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Region.Resident getResident() {
		return resident;
	}

	public Region getRegion() {
		return resident.getRegion().get();
	}

}
