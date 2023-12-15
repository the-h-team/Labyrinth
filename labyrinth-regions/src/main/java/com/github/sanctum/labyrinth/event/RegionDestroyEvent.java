package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.container.Region;
import org.bukkit.block.Block;

public class RegionDestroyEvent extends RegionInteractEvent {

	private final Block block;

	public RegionDestroyEvent(org.bukkit.entity.Player player, Region region, Block block) {
		super(Type.BREAK, player, region);
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}

}
