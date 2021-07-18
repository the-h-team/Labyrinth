package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class RegionPVPEvent extends RegionInteractEvent {

	private final org.bukkit.entity.Player target;

	public RegionPVPEvent(org.bukkit.entity.Player player, org.bukkit.entity.Player target, Region region) {
		super(Type.PVP, player, region);
		this.target = target;
	}

	public org.bukkit.entity.Player getTarget() {
		return target;
	}

	public Optional<ItemStack> getMainHand() {
		return Optional.of(getPlayer().getInventory().getItemInMainHand());
	}

	public Optional<ItemStack> getOffHand() {
		return Optional.of(getPlayer().getInventory().getItemInOffHand());
	}

}
