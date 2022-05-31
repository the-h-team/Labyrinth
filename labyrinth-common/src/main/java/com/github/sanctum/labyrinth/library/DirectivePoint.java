package com.github.sanctum.labyrinth.library;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author Hempfest
 */
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

	public BlockFace toFace() {
		switch (this) {
			case North_East:
				return BlockFace.NORTH_EAST;
			case North:
				return BlockFace.NORTH;
			case North_West:
				return BlockFace.NORTH_WEST;
			case West:
				return BlockFace.WEST;
			case South_West:
				return BlockFace.SOUTH_WEST;
			case South:
				return BlockFace.SOUTH;
			case South_East:
				return BlockFace.SOUTH_EAST;
			case East:
				return BlockFace.EAST;
		}
		return null;
	}

	public Chunk getChunk(Location start, int distanceInChunks) {
		double p = ((pitch + 90) * Math.PI) / 180;
		double ya = ((yaw + 90) * Math.PI) / 180;
		int dist = 16 * distanceInChunks;
		start.setYaw(getCenteredYaw());
		start.setPitch(getCenteredPitch());
		double x = Math.sin(p) * Math.cos(ya);
		double y = Math.sin(p) * Math.sin(ya);
		double z = Math.cos(p);
		start.setDirection(new Vector(x, y, z));
		start.setYaw(getCenteredYaw());
		start.setPitch(getCenteredPitch());
		Location finish = start.add(start.getDirection().normalize().multiply(dist));
		return finish.getChunk();
	}

	public Location getLocation(Location start, int distanceInBlocks) {
		double p = ((pitch + 90) * Math.PI) / 180;
		double ya = ((yaw + 90) * Math.PI) / 180;
		start.setYaw(getCenteredYaw());
		start.setPitch(getCenteredPitch());
		double x = Math.sin(p) * Math.cos(ya);
		double y = Math.sin(p) * Math.sin(ya);
		double z = Math.cos(p);
		start.setDirection(new Vector(x, y, z));
		start.setYaw(getCenteredYaw());
		start.setPitch(getCenteredPitch());
		return start.add(start.getDirection().normalize().multiply(distanceInBlocks));
	}

	public Chunk getChunk(Chunk start, int distanceInChunks) {
		double p = ((pitch + 90) * Math.PI) / 180;
		double ya = ((yaw + 90) * Math.PI) / 180;
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
		center.setDirection(new Vector(x1, y1, z1));
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
		return get(p.getLocation());
	}

	static DirectivePoint get(Location loc) {
		DirectivePoint result;
		float y = loc.getYaw();
		if (y < 0) {
			y += 360;
		}
		y %= 360;
		int i = (int) ((y + 8) / 22.5);
		switch (i) {
			case 1:
			case 2:
			case 3:
				result = DirectivePoint.South_West;
				break;
			case 4:
				result = DirectivePoint.West;
				break;
			case 5:
			case 6:
			case 7:
				result = DirectivePoint.North_West;
				break;
			case 8:
				result = DirectivePoint.North;
				break;
			case 9:
			case 10:
			case 11:
				result = DirectivePoint.North_East;
				break;
			case 12:
				result = DirectivePoint.East;
				break;
			case 13:
			case 14:
			case 15:
				result = DirectivePoint.South_East;
				break;
			default:
				result = DirectivePoint.South;
				break;
		}
		return result;
	}

}
