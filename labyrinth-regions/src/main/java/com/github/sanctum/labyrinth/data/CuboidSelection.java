package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.data.container.Cuboid;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents that of a cuboid selection in emphasis. It contains area data for easy manipulation with regions.
 */
public class CuboidSelection {

	private static final Set<CuboidSelection> cache = new HashSet<>();

	private final Player wizard;
	private ItemStack wand;
	private Location pos1;
	private Location pos2;

	protected CuboidSelection(Player wizard) {
		this.wizard = wizard;
		this.wand = RegionServicesManager.getInstance().getWand();
	}

	public Player getPlayer() {
		return wizard;
	}

	public ItemStack getWand() {
		return wand;
	}

	public boolean isEmpty() {
		return getPos1() == null || getPos2() == null;
	}

	public void setWand(ItemStack wand) {
		this.wand = wand;
	}

	public Location getPos1() {
		return pos1;
	}

	public Location getPos2() {
		return pos2;
	}

	public Location getHighest() {
		return getPos1().getBlockY() > getPos2().getBlockY() ? getPos1() : getPos2();
	}

	public Location getLowest() {
		return getPos2().getBlockY() < getPos1().getBlockY() ? getPos2() : getPos1();
	}

	public Location expand(BlockFace direction) {
		Location update;
		switch (direction) {
			case UP:
				update = getHighest().getBlock().getRelative(BlockFace.UP).getLocation();
				setPos1(update);
				return update;
			case DOWN:
				update = getHighest().getBlock().getRelative(BlockFace.DOWN).getLocation();
				setPos2(update);
				return update;
			case EAST:
				update = getHighest().getBlock().getRelative(BlockFace.EAST).getLocation();
				setPos1(update);
				return update;
			case WEST:
				update = getHighest().getBlock().getRelative(BlockFace.WEST).getLocation();
				setPos2(update);
				return update;
			case NORTH:
				update = getHighest().getBlock().getRelative(BlockFace.NORTH).getLocation();
				setPos1(update);
				return update;
			case SOUTH:
				update = getHighest().getBlock().getRelative(BlockFace.SOUTH).getLocation();
				setPos2(update);
				return update;
			default:
				throw new IllegalStateException();
		}
	}

	public Location expand(BlockFace direction, int distance) {
		Location update;
		switch (direction) {
			case UP:
				update = getHighest().getBlock().getRelative(BlockFace.UP, distance).getLocation();
				setPos1(update);
				return update;
			case DOWN:
				update = getHighest().getBlock().getRelative(BlockFace.DOWN, distance).getLocation();
				setPos2(update);
				return update;
			case EAST:
				update = getHighest().getBlock().getRelative(BlockFace.EAST, distance).getLocation();
				setPos1(update);
				return update;
			case WEST:
				update = getHighest().getBlock().getRelative(BlockFace.WEST, distance).getLocation();
				setPos2(update);
				return update;
			case NORTH:
				update = getHighest().getBlock().getRelative(BlockFace.NORTH, distance).getLocation();
				setPos1(update);
				return update;
			case SOUTH:
				update = getHighest().getBlock().getRelative(BlockFace.SOUTH, distance).getLocation();
				setPos2(update);
				return update;
			default:
				throw new IllegalStateException();
		}
	}

	public void setPos1(Location pos1) {
		this.pos1 = pos1;
	}

	public void setPos2(Location pos2) {
		this.pos2 = pos2;
	}

	public Cuboid toCuboid() {
		return Cuboid.fromPoints(getPos1(), getPos2());
	}

	public static boolean has(Player p) {
		return cache.stream().anyMatch(s -> s.getPlayer().equals(p));
	}

	public static CuboidSelection get(Player p) {
		for (CuboidSelection r : cache) {
			if (r.getPlayer().equals(p)) {
				return r;
			}
		}
		CuboidSelection r = new CuboidSelection(p);
		cache.add(r);
		return r;
	}

}
