package com.github.sanctum.labyrinth.library;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public enum DirectivePoint {

	North_East(226.65f, -1.20f),
	North(179.84f, -0.30f),
	North_West(135.29f, -1.80f),
	West(90.59f, -0.75f),
	South_West(43.04f, 0.59f),
	South(359.24f, 2.54f),
	South_East(318.89f, 1.79f),
	East(269.69f, 0.44f);

	private final float yaw;

	private final float pitch;

	DirectivePoint(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public float getCenteredYaw() {
		return yaw;
	}

	public float getCenteredPitch() {
		return pitch;
	}

	public Chunk getChunk(Location start, int distanceInChunks) {
		int dist = 16 * distanceInChunks;
		start.setYaw(getCenteredYaw());
		Vector target = start.toVector();
		Location finish = start.add(target.multiply(dist));
		return finish.getChunk();
	}

	public Location getLocation(Location start, int distanceInBlocks) {
		start.setYaw(getCenteredYaw());
		Vector target = start.toVector();
		return start.add(target.multiply(distanceInBlocks));
	}

	public Chunk getChunk(Chunk start, int distanceInChunks) {
		int x = start.getX();
		int y = 85;
		int z = start.getZ();
		String world = start.getWorld().getName();
		Location center = new Location(Bukkit.getWorld(world), (x << 4), y, (z << 4)).add(7.0D, 0.0D, 7.0D);
		int dist = 16 * distanceInChunks;
		center.setYaw(getCenteredYaw());
		Vector target = center.toVector();
		Location finish = center.add(target.multiply(dist));
		return finish.getChunk();
	}

	public static DirectivePoint get(Player p) {
		DirectivePoint result = null;
		float y = p.getLocation().getYaw();
		if (y < 0) {
			y += 360;
		}
		y %= 360;
		int i = (int) ((y + 8) / 22.5);
		if (i == 0) {
			result = DirectivePoint.South;
		}
		if (i == 1) {
			result = DirectivePoint.South_West;
		}
		if (i == 2) {
			result = DirectivePoint.South_West;
		}
		if (i == 3) {
			result = DirectivePoint.South_West;
		}
		if (i == 4) {
			result = DirectivePoint.West;
		}
		if (i == 5) {
			result = DirectivePoint.North_West;
		}
		if (i == 6) {
			result = DirectivePoint.North_West;
		}
		if (i == 7) {
			result = DirectivePoint.North_West;
		}
		if (i == 8) {
			result = DirectivePoint.North;
		}
		if (i == 9) {
			result = DirectivePoint.North_East;
		}
		if (i == 10) {
			result = DirectivePoint.North_East;
		}
		if (i == 11) {
			result = DirectivePoint.North_East;
		}
		if (i == 12) {
			result = DirectivePoint.East;
		}
		if (i == 13) {
			result = DirectivePoint.South_East;
		}
		if (i == 14) {
			result = DirectivePoint.South_East;
		}
		if (i == 15) {
			result = DirectivePoint.South_East;
		}
		if (result == null) {
			result = DirectivePoint.South;
		}
		return result;
	}

}
