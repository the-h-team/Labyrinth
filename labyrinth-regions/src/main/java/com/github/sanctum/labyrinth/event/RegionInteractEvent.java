package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;

public abstract class RegionInteractEvent extends DefaultEvent.Player {

	private final Region region;
	private final Type type;
	private boolean cancelled;

	public RegionInteractEvent(Type type, org.bukkit.entity.Player player, Region region) {
		super(player, false);
		this.region = region;
		this.type = type;
	}

	public Region getRegion() {
		return region;
	}

	public Type getInteractionType() {
		return type;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	public enum Type {
		BUILD, BREAK, PVP, INTERACT
	}


}
