package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.interfacing.Catchable;
import com.github.sanctum.labyrinth.interfacing.Snapshot;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.HUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class Region implements Cuboid, Snapshot, Catchable<Region> {

	private Region parent;
	private final int xMin;
	private final int xMax;
	private final int yMin;
	private final int yMax;
	private final int zMin;
	private final int zMax;
	private final int totalSize;
	private final double distanceBetweenPoints;
	private final World world;
	private final Location point1;
	private final Location point2;
	private final HUID id;
	private final int height;
	private final int xWidth;
	private final int zWidth;
	private UUID owner;
	private String name;
	private boolean passthrough;
	private final Plugin plugin;
	protected final List<Flag> FLAGS;
	private final List<UUID> MEMBERS;
	private final List<Block> list;

	protected Region(Region cuboid, Region parent) {
		this(cuboid);
		this.parent = parent;
	}

	protected Region(Region cuboid) {
		this.parent = cuboid.parent;
		this.xMin = cuboid.xMin;
		this.xMax = cuboid.xMax;
		this.yMin = cuboid.yMin;
		this.yMax = cuboid.yMax;
		this.zMin = cuboid.zMin;
		this.zMax = cuboid.zMax;
		this.height = this.yMax - this.yMin + 1;
		this.xWidth = this.xMax - this.xMin + 1;
		this.zWidth = this.zMax - this.zMin + 1;
		this.totalSize = this.getHeight() * this.getXWidth() * this.getZWidth();
		this.world = cuboid.world;
		this.id = cuboid.id;
		this.FLAGS = cuboid.FLAGS;
		this.MEMBERS = cuboid.MEMBERS;
		this.plugin = cuboid.plugin;
		this.list = cuboid.list;
		this.owner = cuboid.owner;
		this.name = cuboid.name;
		this.passthrough = cuboid.passthrough;
		this.point1 = new Location(this.world, this.xMin, this.yMin, this.zMin);
		this.point2 = new Location(this.world, this.xMax, this.yMax, this.zMax);
		this.distanceBetweenPoints = this.getStartingPoint().distance(this.getEndingPoint());
	}

	protected Region(final Location point1, final Location point2) {
		this(point1, point2, HUID.randomID());
	}

	protected Region(final Location point1, final Location point2, HUID id) {
		this(point1, point2, LabyrinthProvider.getInstance().getPluginInstance(), id);
	}

	protected Region(final Location point1, final Location point2, Plugin plugin) {
		this(point1, point2, plugin, HUID.randomID());
	}

	protected Region(final Location point1, final Location point2, Plugin plugin, HUID id) {
		this(point1.getWorld(), Math.min(point1.getBlockX(), point2.getBlockX()), Math.max(point1.getBlockX(), point2.getBlockX()), Math.min(point1.getBlockY(), point2.getBlockY()), Math.max(point1.getBlockY(), point2.getBlockY()), Math.min(point1.getBlockZ(), point2.getBlockZ()), Math.max(point1.getBlockZ(), point2.getBlockZ()), plugin, id);
	}

	protected Region(World world, int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, HUID id) {
		this(world, xMin, xMax, yMin, yMax, zMin, zMax, LabyrinthProvider.getInstance().getPluginInstance(), id);
	}

	protected Region(World world, int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, Plugin plugin, HUID id) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
		this.height = this.yMax - this.yMin + 1;
		this.xWidth = this.xMax - this.xMin + 1;
		this.zWidth = this.zMax - this.zMin + 1;
		this.totalSize = this.getHeight() * this.getXWidth() * this.getZWidth();
		this.world = world;
		this.id = id;
		this.FLAGS = new ArrayList<>();
		this.MEMBERS = new ArrayList<>();
		this.plugin = plugin;
		this.list = new ArrayList<>(this.getTotalBlocks());
		for (int x = this.xMin; x <= this.xMax; x++) {
			for (int y = this.yMin; y <= this.yMax; y++) {
				for (int z = this.zMin; z <= this.zMax; z++) {
					Block b = this.world.getBlockAt(x, y, z);
					list.add(b);
				}
			}
		}
		this.point1 = new Location(this.world, this.xMin, this.yMin, this.zMin);
		this.point2 = new Location(this.world, this.xMax, this.yMax, this.zMax);
		this.distanceBetweenPoints = this.getStartingPoint().distance(this.getEndingPoint());
		for (Flag registered : RegionServicesManager.getInstance().getFlagManager().getFlags()) {
			addFlag(registered);
		}
	}

	@Override
	public Region getSnapshot() {
		return new Region(this, this) {
		};
	}

	@Override
	public boolean update() throws IllegalArgumentException {
		if (parent == null) return false;
		if (!parent.id.equals(id)) throw new IllegalArgumentException("Region snapshot mismatch!.");
		boolean updated = false;
		if (!parent.MEMBERS.containsAll(MEMBERS)) {
			parent.MEMBERS.clear();
			parent.MEMBERS.addAll(MEMBERS);
			updated = true;
		}
		if (!parent.FLAGS.containsAll(FLAGS)) {
			parent.FLAGS.clear();
			parent.FLAGS.addAll(FLAGS);
			updated = true;
		}
		if (!parent.owner.equals(owner)) {
			parent.setOwner(owner);
			updated = true;
		}
		if (!parent.getName().equals(getName())) {
			parent.setName(name);
			updated = true;
		}
		return updated;
	}

	public final Plugin getPlugin() {
		return this.plugin;
	}

	public HUID getId() {
		return this.id;
	}

	public List<Block> getBlocks() {
		return Collections.unmodifiableList(list);
	}

	public List<Region> getLaced() {
		return CompletableFuture.supplyAsync(() -> {
			List<Region> list = new ArrayList<>();
			RegionServicesManager.getInstance().getAll().forEach(c -> {
				for (Block b : this.list) {
					if (c.contains(b.getLocation()) && !c.getId().equals(getId())) {
						list.add(c);
					}
				}
			});
			return list;
		}).join();
	}

	public Location getHighpoint() {
		return getStartingPoint().getBlockY() > getEndingPoint().getBlockY() ? getStartingPoint() : getEndingPoint();
	}

	public Location getLowpoint() {
		return getEndingPoint().getBlockY() < getStartingPoint().getBlockY() ? getEndingPoint() : getStartingPoint();
	}

	public Location getCenter() {
		return new Location(this.world, (double) (this.xMax - this.xMin) / 2 + this.xMin, (double) (this.yMax - this.yMin) / 2 + this.yMin, (double) (this.zMax - this.zMin) / 2 + this.zMin);
	}

	public double getDistanceBetweenPoints() {
		return this.distanceBetweenPoints;
	}

	public Location getStartingPoint() {
		return point1;
	}

	public Location getEndingPoint() {
		return point2;
	}

	public Location getRandomWithin() {
		Random r = new Random();
		int x = r.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
		int y = r.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
		int z = r.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;
		return new Location(this.world, x, y, z);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name != null ? name : id.toString();
	}

	@Override
	public Boundary getBoundary(Player target) {
		return new Boundary(xMax + 0.5, xMin + 0.5, yMax + 0.5, yMin + 0.5, zMax + 0.5, zMin + 0.5).target(target);
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public int getHeight() {
		return height;
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

	public OfflinePlayer getOwner() {
		return owner != null ? Bukkit.getOfflinePlayer(owner) : null;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public List<OfflinePlayer> getMembers() {
		return UniformedComponents.accept(MEMBERS).map(Bukkit::getOfflinePlayer).collect(Collectors.toList());
	}

	public List<Flag> getFlags() {
		return Collections.unmodifiableList(FLAGS);
	}

	public Optional<Flag> getFlag(String id) {
		return this.FLAGS.stream().filter(f -> f.getId().equals(id)).findFirst();
	}

	public boolean isMember(OfflinePlayer p) {
		return UniformedComponents.accept(getMembers()).filter(o -> o.getUniqueId().equals(p.getUniqueId())).findAny().isPresent();
	}

	public boolean hasFlag(String id) {
		return getFlag(id).isPresent();
	}

	public boolean hasFlag(Flag flag) {
		return this.FLAGS.stream().anyMatch(f -> f.getId().equals(flag.getId()));
	}

	public boolean addFlag(Flag flag) {
		if (!hasFlag(flag)) {
			return this.FLAGS.add(flag.clone());
		}
		return false;
	}

	public boolean isPassthrough() {
		return passthrough;
	}

	public void setPassthrough(boolean passthrough) {
		this.passthrough = passthrough;
	}

	public boolean addFlag(Flag... flag) {
		for (Flag f : flag) {
			if (hasFlag(f)) {
				return false;
			} else this.FLAGS.add(f.clone());
		}
		return true;
	}

	public boolean addMember(OfflinePlayer p) {
		if (isMember(p)) {
			return false;
		}
		return MEMBERS.add(p.getUniqueId());
	}

	public boolean addMember(OfflinePlayer... p) {
		for (OfflinePlayer op : p) {
			if (!addMember(op)) {
				return false;
			}
		}
		return true;
	}

	public boolean removeMember(OfflinePlayer p) {
		if (!isMember(p)) {
			return false;
		}
		return MEMBERS.remove(p.getUniqueId());
	}

	public boolean removeFlag(Flag... flag) {
		for (Flag fl : flag) {
			if (!this.FLAGS.removeIf(f -> f.getId().equals(fl.getId()))) {
				return false;
			}
		}
		return true;
	}

	public boolean removeMember(OfflinePlayer... p) {
		for (OfflinePlayer op : p) {
			if (!removeMember(op)) {
				return false;
			}
		}
		return true;
	}

	public boolean contains(final Location loc) {
		return loc.getWorld() == this.world && loc.getBlockX() >= this.xMin && loc.getBlockX() <= this.xMax && loc.getBlockY() >= this.yMin && loc.getBlockY() <= this.yMax && loc
				.getBlockZ() >= this.zMin && loc.getBlockZ() <= this.zMax;
	}

	public boolean contains(final Player player) {
		return this.contains(player.getLocation());
	}

	public boolean contains(final Location loc, double precision) {
		return loc.getWorld() == this.world && loc.getX() >= (xMin() + 0.5) - precision && loc.getX() <= (xMax() + 0.5) + precision && loc.getY() >= (yMin() + 0.5) - precision && loc
				.getY() <= (yMax() + 0.5) + precision && loc.getZ() >= (zMin() + 0.5) - precision && loc.getZ() <= (zMax() + 0.5) + precision;
	}

	public final boolean remove() {
		return RegionServicesManager.getInstance().unload(this);
	}

	public final boolean load() {
		return RegionServicesManager.getInstance().load(this);
	}

	public final Region clone() {
		return new Region(this) {
		};
	}

	public static class Resident {

		public static final List<Resident> LIST = new ArrayList<>();

		private final OfflinePlayer p;

		private boolean spawnTagged;

		private boolean pastSpawn;

		protected Resident(OfflinePlayer player) {
			this.p = player;
			LIST.add(this);
		}

		public static Resident get(OfflinePlayer player) {
			for (Resident resident : LIST) {
				if (resident.getPlayer().equals(player)) {
					return resident;
				}
			}
			Resident r = new Resident(player);
			LIST.add(r);
			return r;
		}

		public OfflinePlayer getPlayer() {
			return p;
		}

		public Player getOnline() {
			return getPlayer().getPlayer();
		}

		public Optional<Region> getRegion() {
			if (getPlayer().isOnline()) {
				return CompletableFuture.supplyAsync(() -> Optional.ofNullable(getPlayer())
						.map(OfflinePlayer::getPlayer)
						.map(Player::getLocation)
						.flatMap(location -> Optional.ofNullable(RegionServicesManager.getInstance().get(location)))
				).join();
			}
			return Optional.empty();
		}

		public boolean isSpawnTagged() {
			return spawnTagged;
		}

		public boolean isPastSpawn() {
			return pastSpawn;
		}

		public void setPastSpawn(boolean pastSpawn) {
			this.pastSpawn = pastSpawn;
		}

		public void setSpawnTagged(boolean spawnTagged) {
			this.spawnTagged = spawnTagged;
		}
	}

}