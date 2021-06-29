package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.library.Cuboid;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

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

	public void expand(Cuboid.Selection.Direction direction) {
		selection.expand(direction);
	}

	public void expand(Cuboid.Selection.Direction direction, int amount) {
		selection.expand(direction, amount);
	}

}
