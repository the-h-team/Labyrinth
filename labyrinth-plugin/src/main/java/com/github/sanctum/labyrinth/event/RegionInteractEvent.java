package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public abstract class RegionInteractEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final Region region;
	private final Type type;
	private boolean cancelled;

	public RegionInteractEvent(Type type, Player player, Region region) {
		super(player);
		this.player = player;
		this.region = region;
		this.type = type;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Region getRegion() {
		return region;
	}

	public Type getType() {
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
		BUILD, BREAK, PVP, LEFT_CLICK, RIGHT_CLICK
	}


}
