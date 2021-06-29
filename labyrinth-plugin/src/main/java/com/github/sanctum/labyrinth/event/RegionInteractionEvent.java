package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;
import org.bukkit.block.Block;

public class RegionInteractionEvent extends RegionInteractEvent {

	private final Block block;

	private final ClickType clickType;

	public RegionInteractionEvent(org.bukkit.entity.Player player, Region region, Block block, ClickType type) {
		super(type == ClickType.LEFT ? Type.LEFT_CLICK : Type.RIGHT_CLICK, player, region);
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
