package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionInteractionEvent extends RegionInteractEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Block block;

	private final ClickType clickType;

	public RegionInteractionEvent(Player player, Region region, Block block, ClickType type) {
		super(type == ClickType.LEFT ? Type.LEFT_CLICK : Type.RIGHT_CLICK, player, region);
		this.block = block;
		this.clickType = type;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
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
