package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class RegionDestroyEvent extends RegionInteractEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Block block;

	public RegionDestroyEvent(Player player, Region region, Block block) {
		super(Type.BREAK, player, region);
		this.block = block;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Block getBlock() {
		return block;
	}

}
