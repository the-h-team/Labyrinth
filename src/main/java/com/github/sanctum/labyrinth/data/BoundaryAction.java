package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.library.Cuboid;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class BoundaryAction {

	private final Player p;
	private final double x;
	private final double y;
	private final double z;

	public BoundaryAction(Player p, double x, double y, double z) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public Player getPlayer() {
		return p;
	}

	public void box() {
		p.spawnParticle(Particle.REDSTONE, getX(), getY(), getZ(), 1, new Particle.DustOptions(Cuboid.Boundary.randomColor(), 2));
		p.spawnParticle(Particle.WATER_DROP, getX(), getY(), getZ(), 1);
	}

	public void walls() {
		p.spawnParticle(Particle.REDSTONE, getX(), getY(), getZ(), 1, new Particle.DustOptions(Cuboid.Boundary.randomColor(), 2));
	}

}
