package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.Region;
import com.github.sanctum.labyrinth.data.RegionFlag;
import com.github.sanctum.labyrinth.data.RegionServicesManager;
import com.github.sanctum.labyrinth.data.SimpleKeyedValue;
import com.github.sanctum.labyrinth.data.service.Check;
import com.github.sanctum.labyrinth.event.RegionBuildEvent;
import com.github.sanctum.labyrinth.event.RegionDestroyEvent;
import com.github.sanctum.labyrinth.event.RegionPVPEvent;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.Random;

/**
 * @author Hempfest
 */
public interface Cuboid {

	static Cuboid fromPoints(Location start, Location end) {
		Check.forNull(start, "Starting point cannot be null!");
		Check.forNull(end, "End point cannot be null!");
		return new Cuboid() {

			private final int xMin;
			private final int xMax;
			private final int yMin;
			private final int yMax;
			private final int zMin;
			private final int zMax;
			private final int height;
			private final int zWidth;
			private final int xWidth;
			private final int totalSize;
			private final World world;

			{
				this.xMin = Math.min(start.getBlockX(), end.getBlockX());
				this.xMax = Math.max(start.getBlockX(), end.getBlockX());
				this.yMin = Math.min(start.getBlockY(), end.getBlockY());
				this.yMax = Math.max(start.getBlockY(), end.getBlockY());
				this.zMin = Math.min(start.getBlockZ(), end.getBlockZ());
				this.zMax = Math.max(start.getBlockZ(), end.getBlockZ());
				this.world = start.getWorld();
				this.height = this.yMax - this.yMin + 1;
				this.xWidth = this.xMax - this.xMin + 1;
				this.zWidth = this.zMax - this.zMin + 1;
				this.totalSize = height * xWidth * zWidth;
			}

			@Override
			public Boundary getBoundary(Player target) {
				return new Boundary(xMax, xMin, yMax, yMin, zMax, zMin);
			}

			@Override
			public World getWorld() {
				return world;
			}

			@Override
			public int getTotalBlocks() {
				return totalSize;
			}

			@Override
			public int getXWidth() {
				return xWidth;
			}

			@Override
			public int getZWidth() {
				return zWidth;
			}

			@Override
			public int getHeight() {
				return height;
			}

			@Override
			public int xMax() {
				return xMax;
			}

			@Override
			public int xMin() {
				return xMin;
			}

			@Override
			public int yMax() {
				return yMax;
			}

			@Override
			public int yMin() {
				return yMin;
			}

			@Override
			public int zMax() {
				return zMax;
			}

			@Override
			public int zMin() {
				return zMin;
			}
		};
	}

	Boundary getBoundary(Player target);

	World getWorld();

	int getTotalBlocks();

	int getXWidth();

	int getZWidth();

	int getHeight();

	int xMax();

	int xMin();

	int yMax();

	int yMin();

	int zMax();

	int zMin();

	default Region toRegion() {
		return new Region(getWorld(), xMin(), xMax(), yMin(), yMax(), zMin(), zMax(), HUID.randomID()){};
	}

	default <R extends Region> R toRegion(Function<SimpleKeyedValue<Cuboid, SimpleKeyedValue<Location, Location>>, R> function) {
		return function.apply(SimpleKeyedValue.of(this, SimpleKeyedValue.of(new Location(getWorld(), xMin(), yMin(), zMin()), new Location(getWorld(), xMax(), yMax(), this.zMax()))));
	}

	class Selection {

		private static final Set<Selection> cache = new HashSet<>();

		private final Player wizard;

		private ItemStack wand = new ItemStack(Material.WOODEN_AXE);

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
			return wand;
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
			return fromPoints(getPos1(), getPos2());
		}

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
			return new RegionFlag(this);
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

	class FlagManager {

		private final Plugin plugin = LabyrinthProvider.getInstance().getPluginInstance();
		private final Set<Flag> CACHE = new HashSet<>();
		private final RegionServicesManager regionServices;

		@SuppressWarnings("OptionalGetWithoutIsPresent") // TODO: Refactor to avoid unchecked #get/safely operate on Optionals
		public FlagManager(RegionServicesManager manager) {
			this.regionServices = manager;
			Flag BREAK = RegionFlag.Builder
					.initialize()
					.label("break")
					.envelope(new Vent.Subscription<>(RegionDestroyEvent.class, plugin, Vent.Priority.MEDIUM, (e, subscription) -> {
						Region region = e.getRegion();

						if (region.hasFlag(getFlag("break").get())) {
							Flag f = region.getFlag("break").get();
							if (f.isValid()) {
								if (!f.isEnabled()) {
									if (!region.isMember(e.getPlayer()) && !region.getOwner().getUniqueId().equals(e.getPlayer().getUniqueId())) {

										Mailer.empty(e.getPlayer()).chat("&4You can't do this!").deploy();
										e.setCancelled(true);
									}
								}
							}
						}
					}))
					.finish();

			Flag BUILD = RegionFlag.Builder
					.initialize()
					.label("build")
					.envelope(new Vent.Subscription<>(RegionBuildEvent.class, plugin, Vent.Priority.MEDIUM, (e, subscription) -> {
						Region region = e.getRegion();

						if (region.hasFlag(getFlag("build").get())) {
							Flag f = region.getFlag("build").get();
							if (f.isValid()) {
								if (!f.isEnabled()) {
									if (!region.isMember(e.getPlayer()) && !region.getOwner().getUniqueId().equals(e.getPlayer().getUniqueId())) {
										Mailer.empty(e.getPlayer()).chat("&4You can't do this!").deploy();
										e.setCancelled(true);
									}
								}
							}
						}
					}))
					.finish();

			Flag PVP = RegionFlag.Builder
					.initialize()
					.label("pvp")
					.envelope(new Vent.Subscription<>(RegionPVPEvent.class, plugin, Vent.Priority.MEDIUM, (e, subscription) -> {
						Player p = e.getPlayer();

						Mailer msg = Mailer.empty(p);

						Region region = e.getRegion();

						if (region.hasFlag(getFlag("pvp").get())) {
							Flag f = region.getFlag("pvp").get();
							if (f.isValid()) {
								if (!f.isEnabled()) {
									if (!region.isMember(e.getPlayer()) && !region.getOwner().getUniqueId().equals(e.getPlayer().getUniqueId())) {
										msg.chat("&4You can't fight here!").deploy();
										e.setCancelled(true);
									}
								}
							}
						}
					}))
					.finish();

			register(PVP);
			register(BREAK);
			register(BUILD);

		}

		public Optional<Flag> getFlag(String id) {
			return CACHE.stream().filter(f -> f.getId().equals(id)).findFirst();
		}

		public Set<Flag> getFlags() {
			return Collections.unmodifiableSet(CACHE);
		}

		public boolean isRegistered(Cuboid.Flag flag) {
			return CACHE.stream().anyMatch(f -> f.getId().equals(flag.getId()));
		}

		public boolean unregister(Cuboid.Flag flag) {
			for (Region r : regionServices.getAll()) {
				r.getFlags().forEach(f -> {
					if (f.getId().equals(flag.getId())) {
						TaskScheduler.of(() -> r.removeFlag(f)).schedule();
					}
				});
			}
			return CACHE.removeIf(f -> f.getId().equals(flag.getId()));
		}

		public boolean register(Cuboid.Flag flag) {
			if (!getFlag(flag.getId()).isPresent()) {
				regionServices.getAll().forEach(region -> region.addFlag(flag));
				return CACHE.add(flag);
			}
			return false;
		}

		public boolean registerControlling(Cuboid.Flag flag) {
			if (!getFlag(flag.getId()).isPresent()) {
				LabyrinthProvider.getService(Service.VENT).subscribe(plugin, flag);
				regionServices.getAll().forEach(region -> region.addFlag(flag));
				return CACHE.add(flag);
			}
			return false;
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
				if (this != CUSTOM)
					throw new IllegalStateException("Invalid particle color usage. Expected 'CUSTOM'");
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
				p.spawnParticle(org.bukkit.Particle.REDSTONE, getX(), getY(), getZ(), 1, new org.bukkit.Particle.DustOptions(randomColor(), 2));
				p.spawnParticle(org.bukkit.Particle.WATER_DROP, getX(), getY(), getZ(), 1);
			}

			public void walls() {
				p.spawnParticle(org.bukkit.Particle.REDSTONE, getX(), getY(), getZ(), 1, new org.bukkit.Particle.DustOptions(randomColor(), 2));
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