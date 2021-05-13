package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.data.BoundaryAction;
import com.github.sanctum.labyrinth.data.BoundaryAssembly;
import com.github.sanctum.labyrinth.data.Region;
import com.github.sanctum.labyrinth.data.RegionFlag;
import com.github.sanctum.labyrinth.data.RegionService;
import com.github.sanctum.labyrinth.event.RegionBuildEvent;
import com.github.sanctum.labyrinth.event.RegionDestroyEvent;
import com.github.sanctum.labyrinth.event.RegionPVPEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
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

	abstract class Flag implements RegionService, Cloneable {

		private final Plugin plugin;
		private boolean allowed;
		private final String id;
		protected String message;

		public Flag(Flag flag) {
			this.plugin = flag.getHost();
			this.id = flag.getId();
			this.message = flag.getMessage();
			this.allowed = flag.allowed;
		}

		public Flag(Plugin plugin, String id, String message) {
			plugin.getLogger().warning("- New flag " + '"' + id + '"' + " registered for public Labyrinth use.");
			this.plugin = plugin;
			this.id = id;
			this.message = message;
			this.allowed = true;
		}

		@Override
		public Flag clone() {
			return new RegionFlag(this);
		}

		public final void setAllowed(boolean allowed) {
			// here we will also set the config value for this flag and save it to the public flag file.
			this.allowed = allowed;
		}

		public final boolean isAllowed() {
			return this.allowed;
		}

		public final boolean isDefault() {
			return getHost().getName().equals("Labyrinth");
		}

		public final boolean isValid() {
			return (this.plugin != null && this.plugin.isEnabled());
		}

		public String getMessage() {
			return StringUtils.use(this.message).translate();
		}

		public String getId() {
			return this.id;
		}

		public Plugin getHost() {
			return this.plugin;
		}

	}

	class FlagManager {

		private final LinkedList<Cuboid.Flag> CUSTOM = new LinkedList<>();

		private final Cuboid.Flag BREAK;
		private final Cuboid.Flag BUILD;
		private final Cuboid.Flag PVP;

		public FlagManager() {

			BREAK = RegionFlag.Builder.initialize(Labyrinth.getInstance())
					.label("break")
					.receive("&4You cant do this!")
					.envelope(new RegionService() {
						@EventHandler(priority = EventPriority.NORMAL)
						public void onBuild(RegionDestroyEvent e) {

							Region region = e.getRegion();

							if (region.hasFlag(BREAK)) {
								Cuboid.Flag f = region.getFlag(BREAK.getId()).get();
								if (f.isValid()) {
									if (!f.isAllowed()) {
										if (!region.isMember(e.getPlayer()) && !region.getOwner().getUniqueId().equals(e.getPlayer().getUniqueId())) {
											Message.form(e.getPlayer()).send(BREAK.getMessage());
											e.setCancelled(true);
										}
									}
								}
							}

						}
					})
					.finish();

			BUILD = RegionFlag.Builder.initialize(Labyrinth.getInstance())
					.label("build")
					.receive("&4You cant do this!")
					.envelope(new RegionService() {
						@EventHandler(priority = EventPriority.NORMAL)
						public void onBuild(RegionBuildEvent e) {

							Region region = e.getRegion();

							if (region.hasFlag(BUILD)) {
								Cuboid.Flag f = region.getFlag(BUILD.getId()).get();
								if (f.isValid()) {
									if (!f.isAllowed()) {
										if (!region.isMember(e.getPlayer()) && !region.getOwner().getUniqueId().equals(e.getPlayer().getUniqueId())) {
											Message.form(e.getPlayer()).send(BUILD.getMessage());
											e.setCancelled(true);
										}
									}
								}
							}

						}
					})
					.finish();

			PVP = RegionFlag.Builder.initialize(Labyrinth.getInstance())
					.label("pvp")
					.receive("&4You cant fight people here.")
					.envelope(new RegionService() {
						@EventHandler(priority = EventPriority.NORMAL)
						public void onPvP(RegionPVPEvent e) {
							Player p = e.getPlayer();

							Message msg = Message.form(p);

							Region region = e.getRegion();

							if (region.hasFlag(PVP)) {
								Cuboid.Flag f = region.getFlag(PVP.getId()).get();
								if (f.isValid()) {
									if (!f.isAllowed()) {
										if (!region.isMember(e.getPlayer()) && !region.getOwner().getUniqueId().equals(e.getPlayer().getUniqueId())) {
											msg.send(PVP.getMessage());
											e.setCancelled(true);
										}
									}
								}
							}

						}
					})
					.finish();

		}

		public Cuboid.Flag getDefault(FlagType type) {
			switch (type) {
				case PVP:
					return PVP;
				case BREAK:
					return BREAK;
				case BUILD:
					return BUILD;
				default:
					throw new IllegalStateException("Invalid flag type presented.");
			}
		}

		public enum FlagType {
			PVP, BREAK, BUILD
		}

		public List<Cuboid.Flag> getDefault() {
			return CUSTOM.stream().filter(f -> f.getHost().getName().equals(Labyrinth.getInstance().getName())).collect(Collectors.toList());
		}

		public Optional<Cuboid.Flag> getFlag(String id) {
			return CUSTOM.stream().filter(f -> f.getId().equals(id)).findFirst();
		}

		public LinkedList<Cuboid.Flag> getFlags() {
			return CUSTOM;
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