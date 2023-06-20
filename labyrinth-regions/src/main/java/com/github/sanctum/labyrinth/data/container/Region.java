package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.CuboidAxis;
import com.github.sanctum.labyrinth.data.CuboidLocation;
import com.github.sanctum.labyrinth.data.RegionServicesManager;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.interfacing.Catchable;
import com.github.sanctum.labyrinth.interfacing.Snapshot;
import com.github.sanctum.panther.container.ImmutablePantherCollection;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherList;
import com.github.sanctum.panther.util.HUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
	private final double distanceBetweenPoints;
	private final Location point1;
	private final Location point2;
	private final CuboidAxis axis;
	private final CuboidLocation location;
	private final HUID id;
	private UUID owner;
	private String name;
	private boolean dominant;
	private final Plugin plugin;
	protected final List<Flag> FLAGS;
	private final List<UUID> MEMBERS;
	private final PantherList<Block> list;

	protected Region(Cuboid cuboid) {
		if (cuboid instanceof Region) {
			this.parent = ((Region) cuboid);
			this.axis = ((Region) cuboid).axis;
			this.id = ((Region) cuboid).id;
			this.FLAGS = ((Region) cuboid).FLAGS;
			this.MEMBERS = ((Region) cuboid).MEMBERS;
			this.plugin = ((Region) cuboid).plugin;
			this.list = ((Region) cuboid).list;
			this.owner = ((Region) cuboid).owner;
			this.name = ((Region) cuboid).name;
			this.dominant = ((Region) cuboid).dominant;
			this.point1 = ((Region) cuboid).point1;
			this.point2 = ((Region) cuboid).point2;
			this.distanceBetweenPoints = ((Region) cuboid).distanceBetweenPoints;
			this.location = ((Region) cuboid).location;
		} else {
			this.axis = cuboid.getAxis();
			this.location = cuboid.getLocation();
			this.id = HUID.randomID();
			this.FLAGS = new ArrayList<>();
			this.MEMBERS = new ArrayList<>();
			this.plugin = LabyrinthProvider.getInstance().getPluginInstance();
			this.list = new PantherList<>(axis.getTotalSize());
			for (int x = axis.getxMin(); x <= axis.getxMax(); x++) {
				for (int y = axis.getyMin(); y <= axis.getyMax(); y++) {
					for (int z = axis.getzMin(); z <= axis.getzMax(); z++) {
						Block b = location.getWorld().getBlockAt(x, y, z);
						list.add(b);
					}
				}
			}
			this.point1 = new Location(location.getWorld(), axis.getxMin(), axis.getyMin(), axis.getzMin());
			this.point2 = new Location(location.getWorld(), axis.getxMax(), axis.getyMax(), axis.getzMax());
			this.distanceBetweenPoints = this.getStartingPoint().distance(this.getEndingPoint());
			for (Flag registered : RegionServicesManager.getInstance().getFlagManager().getFlags()) {
				addFlag(registered);
			}
		}

	}

	protected Region(Region cuboid, Region parent) {
		this(cuboid);
		this.parent = parent;
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
		this.axis = new CuboidAxis(xMax, xMin, yMax, yMin, zMax, zMin);
		this.location = new CuboidLocation(axis, world);
		this.id = id;
		this.FLAGS = new ArrayList<>();
		this.MEMBERS = new ArrayList<>();
		this.plugin = plugin;
		this.list = new PantherList<>(axis.getTotalSize());
		for (int x = axis.getxMin(); x <= axis.getxMax(); x++) {
			for (int y = axis.getyMin(); y <= axis.getyMax(); y++) {
				for (int z = axis.getzMin(); z <= axis.getzMax(); z++) {
					Block b = location.getWorld().getBlockAt(x, y, z);
					list.add(b);
				}
			}
		}
		this.point1 = new Location(location.getWorld(), axis.getxMin(), axis.getyMin(), axis.getzMin());
		this.point2 = new Location(location.getWorld(), axis.getxMax(), axis.getyMax(), axis.getzMax());
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

	public PantherCollection<Block> getBlocks() {
		return ImmutablePantherCollection.of(list);
	}

	public List<Region> getLaced() {
		List<Region> list = new ArrayList<>();
		RegionServicesManager.getInstance().getAll().forEach(c -> {
			for (Block b : this.list) {
				if (c.contains(b.getLocation()) && !c.getId().equals(getId())) {
					list.add(c);
				}
			}
		});
		return list;
	}

	public Location getHighpoint() {
		return getStartingPoint().getBlockY() > getEndingPoint().getBlockY() ? getStartingPoint() : getEndingPoint();
	}

	public Location getLowpoint() {
		return getEndingPoint().getBlockY() < getStartingPoint().getBlockY() ? getEndingPoint() : getStartingPoint();
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

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name != null ? name : id.toString();
	}

	@Override
	public VisualBoundary getBoundary(Player target) {
		return new VisualBoundary(this.axis.getxMax() + 0.5, this.axis.getxMin() + 0.5, this.axis.getyMax() + 0.5, this.axis.getyMin() + 0.5, this.axis.getzMax() + 0.5, this.axis.getzMin() + 0.5).setViewer(target);
	}

	@Override
	public CuboidAxis getAxis() {
		return this.axis;
	}

	@Override
	public CuboidLocation getLocation() {
		return this.location;
	}

	@Override
	public int getTotalBlocks() {
		return this.axis.getTotalSize();
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

	public boolean isDominant() {
		return this.dominant;
	}

	public void setDominant(boolean dominant) {
		this.dominant = dominant;
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

	public boolean contains(Block block) {
		return block.getWorld() == this.location.getWorld() && block.getX() >= this.axis.getxMin() && block.getX() <= this.axis.getxMax() && block.getY() >= this.axis.getyMin() && block.getY() <= this.axis.getyMax() && block
				.getZ() >= this.axis.getzMin() && block.getZ() <= this.axis.getzMax();
	}

	public boolean contains(Location loc) {
		return loc.getWorld() == this.location.getWorld() && loc.getBlockX() >= this.axis.getxMin() && loc.getBlockX() <= this.axis.getxMax() && loc.getBlockY() >= this.axis.getyMin() && loc.getBlockY() <= this.axis.getyMax() && loc
				.getBlockZ() >= this.axis.getzMin() && loc.getBlockZ() <= this.axis.getzMax();
	}

	public boolean contains(Player player) {
		return this.contains(player.getLocation());
	}

	public boolean contains(Block b, double precision) {
		return contains(b.getLocation(), precision);
	}

	public boolean contains(Player p, double precision) {
		return contains(p.getLocation(), precision);
	}

	public boolean contains(Location loc, double precision) {
		return loc.getWorld() == this.location.getWorld() && loc.getX() >= (axis.getxMin() + 0.5) - precision && loc.getX() <= (axis.getxMax() + 0.5) + precision && loc.getY() >= (axis.getyMin() + 0.5) - precision && loc
				.getY() <= (axis.getyMax() + 0.5) + precision && loc.getZ() >= (axis.getzMin() + 0.5) - precision && loc.getZ() <= (axis.getzMax() + 0.5) + precision;
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