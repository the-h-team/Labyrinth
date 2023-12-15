package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.container.Region;
import org.bukkit.block.Block;

public class RegionBuildEvent extends RegionInteractEvent {

	private final Block block;

	public RegionBuildEvent(org.bukkit.entity.Player player, Region region, Block block) {
		super(Type.BUILD, player, region);
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}
}
