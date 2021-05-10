package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.data.BoundaryAction;
import com.github.sanctum.labyrinth.data.BoundaryAssembly;
import com.github.sanctum.labyrinth.data.Region;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public interface Cuboid {

	String getName();

	Boundary getBoundary();

	World getWorld();

	int getTotalBlockSize();

	int getXWidth();

	int getZWidth();

	int getHeight();

	int xMax();

	int xMin();

	int yMax();

	int yMin();

	int zMax();

	int zMin();

	class Selection {

		private static final List<Selection> cache = new LinkedList<>();

		private final Player wizard;

		private Location pos1;

		private Location pos2;

		protected Selection(Player wizard) {
			this.wizard = wizard;
		}

		public static boolean contains(Player p) {
			return cache.stream().anyMatch(s -> s.getPlayer().equals(p));
		}

		public static Selection source(Player p) {
			for (Selection r : cache) {
				if (r.getPlayer().equals(p)) {
					return r;
				}
			}
			Selection r = new Selection(p);
			cache.add(r);
			return r;
		}

		public Player getPlayer() {
			return wizard;
		}

		public ItemStack getWand() {
			return null;
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

		public Location expand(Direction direction) {
			Location update;
			switch (direction) {
				case UP:
					update = getHighest().getBlock().getRelative(BlockFace.UP).getLocation();
					setPos1(update);
					return update;
				case DOWN:
					update = getLowest().getBlock().getRelative(BlockFace.DOWN).getLocation();
					setPos2(update);
					return update;
				case EAST:
					update = getHighest().getBlock().getRelative(BlockFace.EAST).getLocation();
					setPos1(update);
					return update;
				case WEST:
					update = getLowest().getBlock().getRelative(BlockFace.WEST).getLocation();
					setPos2(update);
					return update;
				case NORTH:
					update = getHighest().getBlock().getRelative(BlockFace.NORTH).getLocation();
					setPos1(update);
					return update;
				case SOUTH:
					update = getLowest().getBlock().getRelative(BlockFace.SOUTH).getLocation();
					setPos2(update);
					return update;
				default:
					throw new IllegalStateException();
			}
		}

		public Location expand(Direction direction, int distance) {
			Location update;
			switch (direction) {
				case UP:
					update = getHighest().getBlock().getRelative(BlockFace.UP, distance).getLocation();
					setPos1(update);
					return update;
				case DOWN:
					update = getLowest().getBlock().getRelative(BlockFace.DOWN, distance).getLocation();
					setPos2(update);
					return update;
				case EAST:
					update = getHighest().getBlock().getRelative(BlockFace.EAST, distance).getLocation();
					setPos1(update);
					return update;
				case WEST:
					update = getLowest().getBlock().getRelative(BlockFace.WEST, distance).getLocation();
					setPos2(update);
					return update;
				case NORTH:
					update = getHighest().getBlock().getRelative(BlockFace.NORTH, distance).getLocation();
					setPos1(update);
					return update;
				case SOUTH:
					update = getLowest().getBlock().getRelative(BlockFace.SOUTH, distance).getLocation();
					setPos2(update);
					return update;
				default:
					throw new IllegalStateException();
			}
		}

		public void setPos1(Location pos1) {
			this.pos1 = pos1;

			if (pos1 != null)
				Message.form(wizard).send("&aFirst position selected @ X:" + pos1.getX() + " Z:" + pos1.getZ());
		}

		public void setPos2(Location pos2) {
			this.pos2 = pos2;

			if (pos2 != null)
				Message.form(wizard).send("&aSecond position selected @ X:" + pos2.getX() + " Z:" + pos2.getZ());
		}

		public Region toRegion() {
			return new Region.Standard(getPos1(), getPos2());
		}

		public enum Direction {
			UP, DOWN, EAST, WEST, NORTH, SOUTH
		}

	}

	class Boundary {

		private final double xMax;
		private final double xMin;
		private final double yMax;
		private final double yMin;
		private final double zMax;
		private final double zMin;

		private Player p;

		public Boundary(double xMax, double xMin, double yMax, double yMin, double zMax, double zMin) {
			this.xMax = xMax;
			this.zMax = zMax;
			this.yMax = yMax;
			this.xMin = xMin;
			this.zMin = zMin;
			this.yMin = yMin;
		}

		public Boundary target(Player target) {
			this.p = target;
			return this;
		}

		public enum Particle {
			WHITE(Color.fromRGB(255, 255, 255)),
			GREEN(Color.fromRGB(66, 245, 102)),
			RED(Color.fromRGB(255, 10, 10)),
			YELLOW(Color.fromRGB(207, 183, 4)),
			CUSTOM(Color.AQUA),
			BLUE(Color.fromRGB(3, 148, 252));

			private final Color color;

			Particle(Color color) {
				this.color = color;
			}

			public Color toColor() {
				return color;
			}

			public Color toColor(int hex) {
				return Color.fromRGB(hex);
			}

		}

		public static Color randomColor() {
			Random r = new Random();
			switch (r.nextInt(25)) {
				case 0:
				case 1:
				case 2:
				case 3:
					return Particle.YELLOW.toColor();
				case 4:
				case 5:
				case 6:
				case 7:
					return Particle.WHITE.toColor();
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
					return Particle.RED.toColor();
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
					return Particle.GREEN.toColor();
				case 18:
				case 19:
				case 20:
				case 21:
				case 22:
				case 23:
				case 24:
					return Particle.BLUE.toColor();
			}
			return Particle.WHITE.toColor();
		}

		public void deploy() {
			org.bukkit.Particle.DustOptions dustOptions = new org.bukkit.Particle.DustOptions(randomColor(), 2);
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = zMin; j < zMax + 1; j++) {
					p.spawnParticle(org.bukkit.Particle.REDSTONE, i, yMax, j, 1, dustOptions);
					p.spawnParticle(org.bukkit.Particle.WATER_DROP, i, yMax, j, 1);
				}
			}
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(org.bukkit.Particle.REDSTONE, i, j, zMin, 1, dustOptions);
					p.spawnParticle(org.bukkit.Particle.WATER_DROP, i, j, zMin, 1);
				}
			}
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(org.bukkit.Particle.REDSTONE, i, j, zMax, 1, dustOptions);
					p.spawnParticle(org.bukkit.Particle.WATER_DROP, i, j, zMax, 1);
				}
			}
			for (double i = zMin; i < zMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(org.bukkit.Particle.REDSTONE, xMin, j, i, 1, dustOptions);
					p.spawnParticle(org.bukkit.Particle.WATER_DROP, xMin, j, i, 1);
				}
			}
			for (double i = zMin; i < zMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(org.bukkit.Particle.REDSTONE, xMax, j, i, 1, dustOptions);
					p.spawnParticle(org.bukkit.Particle.WATER_DROP, xMax, j, i, 1);
				}
			}
		}

		public void deploy(BoundaryAssembly assembly) {
			if (assembly != null) {
				for (double i = xMin; i < xMax + 1; i++) {
					for (double j = zMin; j < zMax + 1; j++) {
						assembly.accept(new BoundaryAction(p, i, yMax, j));
					}
				}
				for (double i = xMin; i < xMax + 1; i++) {
					for (double j = yMin; j < yMax + 1; j++) {
						assembly.accept(new BoundaryAction(p, i, j, zMin));
					}
				}
				for (double i = xMin; i < xMax + 1; i++) {
					for (double j = yMin; j < yMax + 1; j++) {
						assembly.accept(new BoundaryAction(p, i, j, zMax));
					}
				}
				for (double i = zMin; i < zMax + 1; i++) {
					for (double j = yMin; j < yMax + 1; j++) {
						assembly.accept(new BoundaryAction(p, xMin, j, i));
					}
				}
				for (double i = zMin; i < zMax + 1; i++) {
					for (double j = yMin; j < yMax + 1; j++) {
						assembly.accept(new BoundaryAction(p, xMax, j, i));
					}
				}
			}
		}

		public void deploy(Particle color) {
			org.bukkit.Particle.DustOptions dustOptions = new org.bukkit.Particle.DustOptions(color.toColor(), 2);
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = zMin; j < zMax + 1; j++) {
					p.spawnParticle(org.bukkit.Particle.REDSTONE, i, yMax, j, 1, dustOptions);
				}
			}
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(org.bukkit.Particle.REDSTONE, i, j, zMin, 1, dustOptions);
				}
			}
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(org.bukkit.Particle.REDSTONE, i, j, zMax, 1, dustOptions);
				}
			}
			for (double i = zMin; i < zMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(org.bukkit.Particle.REDSTONE, xMin, j, i, 1, dustOptions);
				}
			}
			for (double i = zMin; i < zMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(org.bukkit.Particle.REDSTONE, xMax, j, i, 1, dustOptions);
				}
			}
		}

		public void laser(BoundaryAssembly assembly, Location direction) {
			if (assembly != null) {
				Location origin = p.getEyeLocation();
				Vector target = direction.toVector();
				origin.setDirection(target.subtract(origin.toVector()));
				Vector increase = origin.getDirection().multiply(1.3);
				if (!direction.getChunk().equals(p.getLocation().getChunk())) {
					for (int counter = 0; counter < 10; counter++) {
						Location location = origin.add(increase);
						double x = location.getX();
						double y = location.getY() + 0.5;
						double z = location.getZ();
						assembly.accept(new BoundaryAction(p, x, y, z));
					}
				}
			}
		}
	}

}