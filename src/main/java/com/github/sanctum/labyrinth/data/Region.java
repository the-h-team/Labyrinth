package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.HUID;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Region implements Cuboid, Cloneable {

	public static final FileManager DATA = FileList.search(Labyrinth.getInstance()).find("Regions", "Persistent");

	public static final FileManager OPTIONS = FileList.search(Labyrinth.getInstance()).find("Regions", "Persistent");

	private static final List<Region> RECORD = new LinkedList<>();

	private static final List<Region.Loading> LOADING = new LinkedList<>();

	private static final List<Region.Spawning> SPAWNING = new LinkedList<>();

	private final int xMin;
	private final int xMax;
	private final int yMin;
	private final int yMax;
	private final int zMin;
	private final int zMax;
	private final World world;
	private final HUID id;
	private UUID owner;
	private String name;
	private Plugin plugin;
	protected final List<Flag> FLAGS;
	private final List<UUID> MEMBERS;

	protected Region(Region cuboid) {
		this(cuboid.xMin, cuboid.xMax, cuboid.yMin, cuboid.yMax, cuboid.zMin, cuboid.zMax, cuboid.world, cuboid.id);
		setPlugin(cuboid.plugin);
		setOwner(cuboid.owner);
		setName(cuboid.getName());
		addFlag(cuboid.FLAGS.toArray(new Flag[0]));
		addMember(cuboid.MEMBERS.stream().map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new));
	}

	protected Region(final Location point1, final Location point2) {
		this(point1, point2, HUID.randomID());
	}

	protected Region(final Location point1, final Location point2, HUID id) {
		this(Math.min(point1.getBlockX(), point2.getBlockX()), Math.max(point1.getBlockX(), point2.getBlockX()), Math.min(point1.getBlockY(), point2.getBlockY()), Math.max(point1.getBlockY(), point2.getBlockY()), Math.min(point1.getBlockZ(), point2.getBlockZ()), Math.max(point1.getBlockZ(), point2.getBlockZ()), point1.getWorld(), id);
	}

	protected Region(int xmin, int xmax, int ymin, int ymax, int zmin, int zmax, World world, HUID id) {
		this.xMin = xmin;
		this.xMax = xmax;
		this.yMin = ymin;
		this.yMax = ymax;
		this.zMin = zmin;
		this.zMax = zmax;
		this.world = world;
		this.id = id;
		this.FLAGS = new LinkedList<>();
		this.MEMBERS = new LinkedList<>();
		this.plugin = JavaPlugin.getProvidingPlugin(getClass());
	}

	protected void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	public Plugin getPlugin() {
		return this.plugin;
	}

	public HUID getId() {
		return this.id;
	}

	public Iterator<Block> getBlocksWithin() {
		final List<Block> list = new ArrayList<>(this.getTotalBlockSize());
		for (int x = this.xMin; x <= this.xMax; ++x) {
			for (int y = this.yMin; y <= this.yMax; ++y) {
				for (int z = this.zMin; z <= this.zMax; ++z) {
					final Block b = this.world.getBlockAt(x, y, z);
					list.add(b);
				}
			}
		}
		return list.iterator();
	}

	public List<Region> getLacedRegions() {
		List<Region> list = new LinkedList<>();
		for (Region c : cache().list()) {
			for (Iterator<Block> it = getBlocksWithin(); it.hasNext(); ) {
				Block b = it.next();
				if (c.contains(b.getLocation()) && !c.getId().equals(getId())) {
					list.add(c);
					break;
				}
			}
		}
		return list;
	}

	public Location getHighest() {
		return getStartingPoint().getBlockY() > getEndingPoint().getBlockY() ? getStartingPoint() : getEndingPoint();
	}

	public Location getLowest() {
		return getEndingPoint().getBlockY() < getStartingPoint().getBlockY() ? getEndingPoint() : getStartingPoint();
	}

	public Location getCenter() {
		return new Location(this.world, (double) (this.xMax - this.xMin) / 2 + this.xMin, (double) (this.yMax - this.yMin) / 2 + this.yMin, (double) (this.zMax - this.zMin) / 2 + this.zMin);
	}

	public double getDistanceBetween() {
		return this.getStartingPoint().distance(this.getEndingPoint());
	}

	public Location getStartingPoint() {
		return new Location(this.world, this.xMin, this.yMin, this.zMin);
	}

	public Location getEndingPoint() {
		return new Location(this.world, this.xMax, this.yMax, this.zMax);
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

	@Override
	public String getName() {
		return this.name != null ? name : id.toString();
	}

	@Override
	public Boundary getBoundary() {
		return new Boundary(xMax + 0.5, xMin + 0.5, yMax + 0.5, yMin + 0.5, zMax + 0.5, zMin + 0.5);
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public int getHeight() {
		return this.yMax - this.yMin + 1;
	}

	@Override
	public int getTotalBlockSize() {
		return this.getHeight() * this.getXWidth() * this.getZWidth();
	}

	@Override
	public int getXWidth() {
		return this.xMax - this.xMin + 1;
	}

	@Override
	public int getZWidth() {
		return this.zMax - this.zMin + 1;
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
		return UniformedComponents.accept(FLAGS).sort();
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
		if (!hasFlag(flag) && RegionServicesManager.getInstance().isRegistered(flag)) {
			return this.FLAGS.add(flag.clone());
		}
		if (!hasFlag(flag) && !RegionServicesManager.getInstance().isRegistered(flag) && RegionServicesManager.getInstance().getFlagManager().getDefault().stream().anyMatch(f -> f.getId().equals(flag.getId()))) {
			return this.FLAGS.add(flag.clone());
		}
		return false;
	}

	public boolean addFlag(Flag... flag) {
		for (Flag f : flag) {
			if (!addFlag(f)) {
				return false;
			}
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

	public boolean removeFlag(Flag flag) {
		return this.FLAGS.removeIf(f -> f.getId().equals(flag.getId()));
	}

	public boolean removeFlag(Flag... flag) {
		for (Flag f : flag) {
			if (!removeFlag(f)) {
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

	public boolean remove() {
		return RECORD.remove(this);
	}

	public boolean load() {
		return RECORD.add(this);
	}

	public void save() throws IOException {
		if (this.getStartingPoint() != null && this.getEndingPoint() != null) {
			if (!this.getName().equals(getId().toString())) {
				DATA.getConfig().set("Markers.region." + getId().toString() + ".name", this.getName());
			}
			DATA.getConfig().set("Markers.region." + getId().toString() + ".plugin", this.getPlugin().getName());
			DATA.getConfig().set("Markers.region." + getId().toString() + ".pos1", this.getStartingPoint());
			DATA.getConfig().set("Markers.region." + getId().toString() + ".pos2", this.getEndingPoint());
			DATA.getConfig().set("Markers.region." + getId().toString() + ".owner", this.getOwner().getUniqueId().toString());
			DATA.getConfig().set("Markers.region." + getId().toString() + ".members", MEMBERS.stream().map(UUID::toString).collect(Collectors.toList()));
			for (Flag f : getFlags()) {
				DATA.getConfig().set("Markers.region." + getId().toString() + ".flags." + f.getId() + ".allowed", f.isAllowed());
				DATA.getConfig().set("Markers.region." + getId().toString() + ".flags." + f.getId() + ".plugin", f.isValid() ? f.getHost().getName() : "NA");
				DATA.getConfig().set("Markers.region." + getId().toString() + ".flags." + f.getId() + ".message", f.getMessage());
			}
			DATA.saveConfig();
		} else
			throw new IOException("One or more locations were found null during the saving process.");
	}

	@Override
	public Region clone() {
		return new Standard(this);
	}

	public static UniformedComponents<Region> cache() {
		return UniformedComponents.accept(RECORD);
	}

	public static UniformedComponents<Region.Loading> loading() {
		return UniformedComponents.accept(LOADING);
	}

	public static UniformedComponents<Region.Spawning> spawning() {
		return UniformedComponents.accept(SPAWNING);
	}

	public static Optional<Spawn> spawn() {
		for (Region c : cache().list()) {
			if (c instanceof Spawn) {
				if (((Spawn) c).location() != null)
					return Optional.of((Spawn) c);
			}
		}
		return Optional.empty();
	}

	public static Optional<Region> match(Location location) {
		for (Region c : cache().list()) {
			if (c.contains(location)) {
				return Optional.of(c);
			}
		}
		return Optional.empty();
	}

	public static class Resident {

		public static final List<Resident> LIST = new LinkedList<>();

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
				return Region.match(getPlayer().getPlayer().getLocation());
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

	public static class Spawning extends Region {

		private Location loc;

		protected Spawning(Region cuboid) {
			super(cuboid);
		}

		public Spawning(Location point1, Location point2) {
			super(point1, point2);
		}

		public Spawning(Location point1, Location point2, HUID id) {
			super(point1, point2, id);
		}

		public void setPlugin(Plugin plugin) {
			super.setPlugin(plugin);
		}

		@Override
		public boolean remove() {
			return SPAWNING.remove(this);
		}

		@Override
		public boolean load() {
			return SPAWNING.add(this);
		}

		public Location location() {
			return loc;
		}

		public void setLocation(Location loc) {
			this.loc = loc;
		}

		public List<Spawning> getArea() {
			List<Spawning> list = new LinkedList<>();
			for (Region.Spawning c : SPAWNING) {
				for (Iterator<Block> it = getBlocksWithin(); it.hasNext(); ) {
					Block b = it.next();
					if (c.contains(b.getLocation())) {
						list.add(c);
						break;
					}
				}
			}
			return list;
		}

		@Override
		public void save() throws IOException {
			if (this.getStartingPoint() != null && this.getEndingPoint() != null && this.location() != null) {
				if (!this.getName().equals(getId().toString())) {
					DATA.getConfig().set("Markers.spawn." + getId().toString() + ".name", this.getName());
				}
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".plugin", this.getPlugin().getName());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".pos1", this.getStartingPoint());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".pos2", this.getEndingPoint());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".start", location());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".owner", this.getOwner().getUniqueId().toString());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".members", getMembers().stream().map(OfflinePlayer::getUniqueId).map(UUID::toString).collect(Collectors.toList()));
				for (Flag f : getFlags()) {
					DATA.getConfig().set("Markers.spawn." + getId().toString() + ".flags." + f.getId() + ".allowed", f.isAllowed());
					DATA.getConfig().set("Markers.spawn." + getId().toString() + ".flags." + f.getId() + ".plugin", f.isValid() ? f.getHost().getName() : "NA");
					DATA.getConfig().set("Markers.spawn." + getId().toString() + ".flags." + f.getId() + ".message", f.getMessage());
				}
				DATA.saveConfig();
			} else
				throw new IOException("One or more locations were found null during the saving process.");
		}
	}

	public static class Loading extends Region {

		protected Loading(Region cuboid) {
			super(cuboid);
		}

		public Loading(Location point1, Location point2) {
			super(point1, point2);
		}

		public Loading(Location point1, Location point2, HUID id) {
			super(point1, point2, id);
		}

		public void setPlugin(Plugin plugin) {
			super.setPlugin(plugin);
		}

		@Override
		public boolean remove() {
			return LOADING.remove(this);
		}

		@Override
		public boolean load() {
			return LOADING.add(this);
		}
	}

	public static class Standard extends Region {

		public Standard(Region cuboid) {
			super(cuboid);
			if (cuboid.getFlags().isEmpty()) {
				FLAGS.addAll(RegionServicesManager.getInstance().getFlagManager().getDefault());
			}
		}

		public Standard(Location point1, Location point2) {
			super(point1, point2);
			if (getFlags().isEmpty()) {
				FLAGS.addAll(RegionServicesManager.getInstance().getFlagManager().getDefault());
			}
		}

		public Standard(Location point1, Location point2, HUID id) {
			super(point1, point2, id);
			if (getFlags().isEmpty()) {
				FLAGS.addAll(RegionServicesManager.getInstance().getFlagManager().getDefault());
			}
		}

		public Standard forPlugin(Plugin plugin) {
			super.setPlugin(plugin);
			return this;
		}

	}

	public static class Spawn extends Region {

		private Location loc;

		public Spawn(Region.Spawning cuboid) {
			super(cuboid);
			if (cuboid.getFlags().isEmpty()) {
				FLAGS.addAll(RegionServicesManager.getInstance().getFlagManager().getDefault());
			}
		}

		public Spawn(Location point1, Location point2) {
			super(point1, point2);
			if (getFlags().isEmpty()) {
				FLAGS.addAll(RegionServicesManager.getInstance().getFlagManager().getDefault());
			}
		}

		public Spawn(Location point1, Location point2, HUID id) {
			super(point1, point2, id);
			if (getFlags().isEmpty()) {
				FLAGS.addAll(RegionServicesManager.getInstance().getFlagManager().getDefault());
			}
		}

		public Location location() {
			return loc;
		}

		public void setLocation(Location loc) {
			this.loc = loc;
		}

		public List<Spawn> getArea() {
			List<Spawn> list = new LinkedList<>();
			for (Region c : RECORD) {
				if (c instanceof Spawn) {
					for (Iterator<Block> it = getBlocksWithin(); it.hasNext(); ) {
						Block b = it.next();
						if (c.contains(b.getLocation())) {
							list.add((Spawn) c);
							break;
						}
					}
				}
			}
			return list;
		}

		public Spawn forPlugin(Plugin plugin) {
			super.setPlugin(plugin);
			return this;
		}

		@Override
		public void save() throws IOException {
			if (this.getStartingPoint() != null && this.getEndingPoint() != null && this.location() != null) {
				if (!this.getName().equals(getId().toString())) {
					DATA.getConfig().set("Markers.spawn." + getId().toString() + ".name", this.getName());
				}
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".plugin", this.getPlugin().getName());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".pos1", this.getStartingPoint());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".pos2", this.getEndingPoint());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".start", location());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".owner", this.getOwner().getUniqueId().toString());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".members", getMembers().stream().map(OfflinePlayer::getUniqueId).map(UUID::toString).collect(Collectors.toList()));
				for (Flag f : getFlags()) {
					DATA.getConfig().set("Markers.spawn." + getId().toString() + ".flags." + f.getId() + ".allowed", f.isAllowed());
					DATA.getConfig().set("Markers.spawn." + getId().toString() + ".flags." + f.getId() + ".plugin", f.isValid() ? f.getHost().getName() : "NA");
					DATA.getConfig().set("Markers.spawn." + getId().toString() + ".flags." + f.getId() + ".message", f.getMessage());
				}
				DATA.saveConfig();
			} else
				throw new IOException("One or more locations were found null during the saving process.");
		}

	}

}