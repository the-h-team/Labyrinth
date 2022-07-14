package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.library.Cuboid;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public abstract class CuboidSelectEvent extends DefaultEvent.Player {

	private final Cuboid.Selection selection;

	public CuboidSelectEvent(Cuboid.Selection selection) {
		super(selection.getPlayer(), false);
		this.selection = selection;
	}

	public Optional<Location> getPos1() {
		return Optional.ofNullable(selection.getPos1());
	}

	public Optional<Location> getPos2() {
		return Optional.ofNullable(selection.getPos2());
	}

	public void setPos1(Location loc) {
		selection.setPos1(loc);
	}

	public void setPos2(Location loc) {
		selection.setPos2(loc);
	}

	public ItemStack getWand() {
		return selection.getWand();
	}

	public void expand(BlockFace direction) {
		selection.expand(direction);
	}

	public void expand(BlockFace direction, int amount) {
		selection.expand(direction, amount);
	}

}
