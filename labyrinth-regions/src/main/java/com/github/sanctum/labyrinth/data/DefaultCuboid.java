package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.data.container.Cuboid;
import com.github.sanctum.panther.util.Check;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DefaultCuboid implements Cuboid {

	private final CuboidAxis axis;
	private final CuboidLocation location;

	public DefaultCuboid(Location start, Location end) throws NullPointerException {
		Check.forNull(start, "Starting point cannot be null!");
		Check.forNull(end, "End point cannot be null!");
		Check.argument(start.getWorld().getName().equals(end.getWorld().getName()), "The locations must both be from the same realm!");
		this.axis = new CuboidAxis(start.getBlockX(), end.getBlockX(), start.getBlockY(), end.getBlockY(), start.getBlockZ(), end.getBlockZ());
		this.location = new CuboidLocation(axis, start.getWorld());
	}

	@Override
	public VisualBoundary getBoundary(Player target) {
		return new VisualBoundary(axis.getxMax(), axis.getxMin(), axis.getyMax(), axis.getyMin(), axis.getzMax(), axis.getzMin()).setViewer(target);
	}

	@Override
	public int getTotalBlocks() {
		return axis.getTotalSize();
	}

	@Override
	public CuboidAxis getAxis() {
		return axis;
	}

	@Override
	public CuboidLocation getLocation() {
		return location;
	}
}
