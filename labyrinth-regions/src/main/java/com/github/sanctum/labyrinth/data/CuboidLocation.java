package com.github.sanctum.labyrinth.data;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;

public class CuboidLocation {

	private final World world;
	private final CuboidAxis axis;
	private final Location center;

	public CuboidLocation(CuboidAxis axis, World world) {
		this.world = world;
		this.axis = axis;
		this.center = new Location(getWorld(), (double) (this.axis.getxMax() - this.axis.getxMin()) / 2 + this.axis.getxMin(), (double) (this.axis.getyMax() - this.axis.getyMin()) / 2 + this.axis.getyMin(), (double) (this.axis.getzMax() - this.axis.getzMin()) / 2 + this.axis.getzMin());
	}

	public Location getCenter() {
		return center;
	}

	public Location getRandom() {
		Random r = new Random();
		int x = r.nextInt(Math.abs(this.axis.getxMax() - this.axis.getxMin()) + 1) + this.axis.getxMin();
		int y = r.nextInt(Math.abs(this.axis.getyMax() - this.axis.getyMin()) + 1) + this.axis.getyMin();
		int z = r.nextInt(Math.abs(this.axis.getzMax() - this.axis.getzMin()) + 1) + this.axis.getzMin();
		return new Location(getWorld(), x, y, z);
	}

	public World getWorld() {
		return world;
	}
}
