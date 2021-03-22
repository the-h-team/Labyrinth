package com.github.sanctum.labyrinth.library;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public enum DirectivePoint {

	North_East(225.0f, -0.0f),
	North(180.0f, -0.0f),
	North_West(135.0f, -0.0f),
	West(90.00f, 0.0f),
	South_West(45.0f, 0.0f),
	South(360.0f, 0.0f),
	South_East(315.0f, 0.0f),
	East(270.0f, 0.0f);

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
		double p = ((pitch + 90) * Math.PI) / 180;
		double ya  = ((yaw + 90)  * Math.PI) / 180;
		int dist = 16 * distanceInChunks;
		start.setYaw(getCenteredYaw());
		start.setPitch(getCenteredPitch());
		double x = Math.sin(p) * Math.cos(ya);
		double y = Math.sin(p) * Math.sin(ya);
		double z = Math.cos(p);
		start.setDirection(new Vector(x, y ,z));
		start.setYaw(getCenteredYaw());
		start.setPitch(getCenteredPitch());
		Location finish = start.add(start.getDirection().normalize().multiply(dist));
		return finish.getChunk();
	}

	public Location getLocation(Location start, int distanceInBlocks) {
		double p = ((pitch + 90) * Math.PI) / 180;
		double ya  = ((yaw + 90)  * Math.PI) / 180;
		start.setYaw(getCenteredYaw());
		start.setPitch(getCenteredPitch());
		double x = Math.sin(p) * Math.cos(ya);
		double y = Math.sin(p) * Math.sin(ya);
		double z = Math.cos(p);
		start.setDirection(new Vector(x, y ,z));
		start.setYaw(getCenteredYaw());
		start.setPitch(getCenteredPitch());
		return start.add(start.getDirection().normalize().multiply(distanceInBlocks));
	}

	public Chunk getChunk(Chunk start, int distanceInChunks) {
		double p = ((pitch + 90) * Math.PI) / 180;
		double ya  = ((yaw + 90)  * Math.PI) / 180;
		int x = start.getX();
		int y = 85;
		int z = start.getZ();
		String world = start.getWorld().getName();
		Location center = new Location(Bukkit.getWorld(world), (x << 4), y, (z << 4)).add(7.0D, 0.0D, 7.0D);
		int dist = 16 * distanceInChunks;
		center.setYaw(getCenteredYaw());
		center.setPitch(getCenteredPitch());
		double x1 = Math.sin(p) * Math.cos(ya);
		double y1 = Math.sin(p) * Math.sin(ya);
		double z1 = Math.cos(p);
		center.setDirection(new Vector(x1, y1 ,z1));
		center.setYaw(getCenteredYaw());
		center.setPitch(getCenteredPitch());
		Location finish = center.add(center.getDirection().normalize().multiply(dist));
		return finish.getChunk();
	}

	public static DirectivePoint getLeft(DirectivePoint point) {
		DirectivePoint result = null;
		switch (point) {

			case North_East:
				result = DirectivePoint.North;
				break;
			case North:
				result = DirectivePoint.North_West;
				break;
			case North_West:
				result = DirectivePoint.West;
				break;
			case West:
				result = DirectivePoint.South_West;
				break;
			case South_West:
				result = DirectivePoint.South;
				break;
			case South:
				result = DirectivePoint.South_East;
				break;
			case South_East:
				result = DirectivePoint.East;
				break;
			case East:
				result = DirectivePoint.North_East;
				break;
		}
		return result;
	}

	public static DirectivePoint getRight(DirectivePoint point) {
		DirectivePoint result = null;
		switch (point) {

			case North_East:
				result = DirectivePoint.East;
				break;
			case North:
				result = DirectivePoint.North_East;
				break;
			case North_West:
				result = DirectivePoint.North;
				break;
			case West:
				result = DirectivePoint.North_West;
				break;
			case South_West:
				result = DirectivePoint.West;
				break;
			case South:
				result = DirectivePoint.South_West;
				break;
			case South_East:
				result = DirectivePoint.South;
				break;
			case East:
				result = DirectivePoint.South_East;
				break;
		}
		return result;
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
