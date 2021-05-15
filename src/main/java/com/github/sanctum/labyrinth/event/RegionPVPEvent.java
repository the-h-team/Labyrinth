package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RegionPVPEvent extends RegionInteractEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Player target;

	public RegionPVPEvent(Player player, Player target, Region region) {
		super(Type.BUILD, player, region);
		this.target = target;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getTarget() {
		return target;
	}

	public Optional<ItemStack> getMainHand() {
		return Optional.of(getPlayer().getInventory().getItemInMainHand());
	}

	public Optional<ItemStack> getOffHand() {
		return Optional.of(getPlayer().getInventory().getItemInOffHand());
	}

}
