package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.api.LegacyCheckService;
import com.github.sanctum.labyrinth.data.CuboidAxis;
import com.github.sanctum.labyrinth.data.CuboidLocation;
import com.github.sanctum.labyrinth.data.DefaultCuboid;
import com.github.sanctum.labyrinth.data.DefaultFlag;
import com.github.sanctum.labyrinth.data.RegionServicesManager;
import com.github.sanctum.labyrinth.data.SimpleKeyedValue;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.util.HUID;
import java.util.function.Function;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import java.util.Random;

/**
 * An interface for capturing cubic or rectangular areas.
 *
 * @author Hempfest
 */
public interface Cuboid {

	static Cuboid fromPoints(Location start, Location end) {
		return new DefaultCuboid(start, end);
	}

	VisualBoundary getBoundary(Player target);

	int getTotalBlocks();

	CuboidAxis getAxis();

	CuboidLocation getLocation();

	default Region toRegion() {
		return new Region(getLocation().getWorld(), getAxis().getxMin(), getAxis().getxMax(), getAxis().getyMin(), getAxis().getyMax(), getAxis().getzMin(), getAxis().getzMax(), HUID.randomID()){};
	}

	default <R extends Region> R toRegion(Function<SimpleKeyedValue<Cuboid, SimpleKeyedValue<Location, Location>>, R> function) {
		return function.apply(SimpleKeyedValue.of(this, SimpleKeyedValue.of(new Location(getLocation().getWorld(), getAxis().getxMin(), getAxis().getyMin(), getAxis().getzMin()), new Location(getLocation().getWorld(), getAxis().getxMax(), getAxis().getyMax(), getAxis().getzMax()))));
	}

	abstract class Flag implements Listener {

		private boolean allowed;
		private final String id;

		public Flag(Flag flag) {
			this.id = flag.getId();
			this.allowed = flag.allowed;
		}

		public Flag(String id) {
			this.id = id;
			this.allowed = true;
		}

		public Flag clone() {
			return new DefaultFlag(this);
		}

		public final void setEnabled(boolean allowed) {
			this.allowed = allowed;
		}

		public final boolean isEnabled() {
			return this.allowed;
		}

		public final boolean isDefault() {
			return StringUtils.use(id).containsIgnoreCase("break", "build", "pvp");
		}

		public final boolean isValid() {
			return RegionServicesManager.getInstance().getFlagManager().getFlag(id).isPresent();
		}

		public String getId() {
			return this.id;
		}

	}

	class VisualBoundary {

		final Particle power = LegacyCheckService.VERSION.contains("1_21_R1") ? Particle.valueOf("ELECTRIC_SPARK") : Particle.valueOf("REDSTONE");
		final Particle water = LegacyCheckService.VERSION.contains("1_21_R1") ? Particle.valueOf("DRIPPING_WATER") : Particle.valueOf("WATER_DROP");

		private final double xMax;
		private final double xMin;
		private final double yMax;
		private final double yMin;
		private final double zMax;
		private final double zMin;

		private Player p;

		public VisualBoundary(double xMax, double xMin, double yMax, double yMin, double zMax, double zMin) {
			this.xMax = xMax;
			this.zMax = zMax;
			this.yMax = yMax;
			this.xMin = xMin;
			this.zMin = zMin;
			this.yMin = yMin;
		}

		public VisualBoundary setViewer(Player target) {
			this.p = target;
			return this;
		}

		public enum Color {
			WHITE(org.bukkit.Color.fromRGB(255, 255, 255)),
			GREEN(org.bukkit.Color.fromRGB(66, 245, 102)),
			RED(org.bukkit.Color.fromRGB(255, 10, 10)),
			YELLOW(org.bukkit.Color.fromRGB(207, 183, 4)),
			CUSTOM(org.bukkit.Color.AQUA),
			BLUE(org.bukkit.Color.fromRGB(3, 148, 252));

			private final org.bukkit.Color color;

			Color(org.bukkit.Color color) {
				this.color = color;
			}

			public org.bukkit.Color toColor() {
				return color;
			}

			public org.bukkit.Color toColor(int hex) {
				if (this != CUSTOM)
					throw new IllegalStateException("Invalid particle color usage. Expected 'CUSTOM'");
				return org.bukkit.Color.fromRGB(hex);
			}

		}

		public static org.bukkit.Color randomColor() {
			Random r = new Random();
			switch (r.nextInt(25)) {
				case 0:
				case 1:
				case 2:
				case 3:
					return Color.YELLOW.toColor();
				case 4:
				case 5:
				case 6:
				case 7:
					return Color.WHITE.toColor();
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
					return Color.RED.toColor();
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
					return Color.GREEN.toColor();
				case 18:
				case 19:
				case 20:
				case 21:
				case 22:
				case 23:
				case 24:
					return Color.BLUE.toColor();
			}
			return Color.WHITE.toColor();
		}

		public void deploy() {
			org.bukkit.Particle.DustOptions dustOptions = new org.bukkit.Particle.DustOptions(randomColor(), 2);
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = zMin; j < zMax + 1; j++) {
					p.spawnParticle(power, i, yMax, j, 1, dustOptions);
					p.spawnParticle(water, i, yMax, j, 1);
				}
			}
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(power, i, j, zMin, 1, dustOptions);
					p.spawnParticle(water, i, j, zMin, 1);
				}
			}
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(power, i, j, zMax, 1, dustOptions);
					p.spawnParticle(water, i, j, zMax, 1);
				}
			}
			for (double i = zMin; i < zMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(power, xMin, j, i, 1, dustOptions);
					p.spawnParticle(water, xMin, j, i, 1);
				}
			}
			for (double i = zMin; i < zMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(power, xMax, j, i, 1, dustOptions);
					p.spawnParticle(water, xMax, j, i, 1);
				}
			}
		}

		public void deploy(Assembly assembly) {
			if (assembly != null) {
				for (double i = xMin; i < xMax + 1; i++) {
					for (double j = zMin; j < zMax + 1; j++) {
						assembly.accept(new Action(p, i, yMax, j));
					}
				}
				for (double i = xMin; i < xMax + 1; i++) {
					for (double j = yMin; j < yMax + 1; j++) {
						assembly.accept(new Action(p, i, j, zMin));
					}
				}
				for (double i = xMin; i < xMax + 1; i++) {
					for (double j = yMin; j < yMax + 1; j++) {
						assembly.accept(new Action(p, i, j, zMax));
					}
				}
				for (double i = zMin; i < zMax + 1; i++) {
					for (double j = yMin; j < yMax + 1; j++) {
						assembly.accept(new Action(p, xMin, j, i));
					}
				}
				for (double i = zMin; i < zMax + 1; i++) {
					for (double j = yMin; j < yMax + 1; j++) {
						assembly.accept(new Action(p, xMax, j, i));
					}
				}
			}
		}

		public void deploy(Color color) {
			org.bukkit.Particle.DustOptions dustOptions = new org.bukkit.Particle.DustOptions(color.toColor(), 2);
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = zMin; j < zMax + 1; j++) {
					p.spawnParticle(power, i, yMax, j, 1, dustOptions);
				}
			}
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(power, i, j, zMin, 1, dustOptions);
				}
			}
			for (double i = xMin; i < xMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(power, i, j, zMax, 1, dustOptions);
				}
			}
			for (double i = zMin; i < zMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(power, xMin, j, i, 1, dustOptions);
				}
			}
			for (double i = zMin; i < zMax + 1; i++) {
				for (double j = yMin; j < yMax + 1; j++) {
					p.spawnParticle(power, xMax, j, i, 1, dustOptions);
				}
			}
		}

		public void laser(Assembly assembly, Location direction) {
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
						assembly.accept(new Action(p, x, y, z));
					}
				}
			}
		}

		public static class Action {

			final Particle power = LegacyCheckService.VERSION.contains("1_21_R1") ? Particle.valueOf("ELECTRIC_SPARK") : Particle.valueOf("REDSTONE");
			final Particle water = LegacyCheckService.VERSION.contains("1_21_R1") ? Particle.valueOf("DRIPPING_WATER") : Particle.valueOf("WATER_DROP");
			private final Player p;
			private final double x;
			private final double y;
			private final double z;

			public Action(Player p, double x, double y, double z) {
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
				p.spawnParticle(power, getX(), getY(), getZ(), 1, new org.bukkit.Particle.DustOptions(randomColor(), 2));
				p.spawnParticle(water, getX(), getY(), getZ(), 1);
			}

			public void walls() {
				p.spawnParticle(power, getX(), getY(), getZ(), 1, new org.bukkit.Particle.DustOptions(randomColor(), 2));
			}

			public void box(Material material, long decay) {
				Location location = new Location(p.getWorld(), getX(), getY(), getZ());
				final BlockData og = location.getBlock().getState().getBlockData().getMaterial().createBlockData();
				p.sendBlockChange(location, material.createBlockData());
				TaskScheduler.of(() -> p.sendBlockChange(location, og)).scheduleLater(decay);
			}

		}

		@FunctionalInterface
		public interface Assembly {

			void accept(Action action);

		}
	}

}