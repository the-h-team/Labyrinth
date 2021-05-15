package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionBuildEvent extends RegionInteractEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Block block;

	public RegionBuildEvent(Player player, Region region, Block block) {
		super(Type.BUILD, player, region);
		this.block = block;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Block getBlock() {
		return block;
	}
}
