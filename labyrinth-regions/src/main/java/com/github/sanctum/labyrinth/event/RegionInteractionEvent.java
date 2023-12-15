package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.container.Region;
import org.bukkit.block.Block;

public class RegionInteractionEvent extends RegionInteractEvent {

	private final Block block;

	private final ClickType clickType;

	public RegionInteractionEvent(org.bukkit.entity.Player player, Region region, Block block, ClickType type) {
		super(Type.INTERACT, player, region);
		this.block = block;
		this.clickType = type;
	}

	public ClickType getClickType() {
		return clickType;
	}

	public Block getBlock() {
		return block;
	}

	public enum ClickType {
		RIGHT, LEFT
	}

}
