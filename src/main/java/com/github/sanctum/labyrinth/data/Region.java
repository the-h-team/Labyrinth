package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.HUID;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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

public abstract class Region implements Cuboid, Cloneable {

	public static final FileManager DATA = FileList.search(Labyrinth.getInstance()).find("Regions", "");

	private static final List<Region> RECORD = new LinkedList<>();

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
	protected final Collection<Flag> FLAGS;

	private final List<UUID> members;

	protected Region(Region cuboid) {
		this(cuboid.xMin, cuboid.xMax, cuboid.yMin, cuboid.yMax, cuboid.zMin, cuboid.zMax, cuboid.world, cuboid.id);
		setOwner(cuboid.owner);
		setName(cuboid.getName());
		addFlag(cuboid.FLAGS.toArray(new Flag[0]));
		addMember(cuboid.members.stream().map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new));
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
		this.FLAGS = new HashSet<>();
		this.members = new LinkedList<>();
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
				if (c.contains(b.getLocation())) {
					list.add(c);
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
		return UniformedComponents.accept(members).map(Bukkit::getOfflinePlayer).collect(Collectors.toList());
	}

	public boolean isMember(OfflinePlayer p) {
		return UniformedComponents.accept(getMembers()).filter(o -> o.getUniqueId().equals(p.getUniqueId())).findAny().isPresent();
	}

	public boolean addFlag(Flag flag) {
		if (!this.FLAGS.contains(flag)) {
			return this.FLAGS.add(flag);
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
		return members.add(p.getUniqueId());
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
		return members.remove(p.getUniqueId());
	}

	public boolean removeFlag(Flag flag) {
		return this.FLAGS.remove(flag);
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
			DATA.getConfig().set("Markers.region." + getId().toString() + ".pos1", this.getStartingPoint());
			DATA.getConfig().set("Markers.region." + getId().toString() + ".pos2", this.getEndingPoint());
			DATA.getConfig().set("Markers.region." + getId().toString() + ".owner", this.getOwner().getUniqueId().toString());
			DATA.getConfig().set("Markers.region." + getId().toString() + ".members", members.stream().map(UUID::toString).collect(Collectors.toList()));
			DATA.getConfig().set("Markers.region." + getId().toString() + ".flags", FLAGS.stream().map(Enum::name).collect(Collectors.toList()));
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

	public static Optional<Spawn> spawn() {
		for (Region c : cache().list()) {
			if (c instanceof Spawn) {
				if (((Spawn) c).location() != null)
					return Optional.of((Spawn) c);
			}
		}
		return Optional.empty();
	}

	public static boolean match(Location location) {
		for (Region c : cache().list()) {
			if (c.contains(location)) {
				return true;
			}
		}
		return false;
	}

	public static Region get(Location location) {
		for (Region c : cache().list()) {
			if (c.contains(location)) {
				return c;
			}
		}
		return null;
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

		public Optional<Region> getRegion() {
			if (getPlayer().isOnline()) {
				if (Region.match(getPlayer().getPlayer().getLocation())) {
					return Optional.ofNullable(Region.get(getPlayer().getPlayer().getLocation()));
				}
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

	public static class Standard extends Region {

		protected Standard(Region cuboid) {
			super(cuboid);
		}

		public Standard(Location point1, Location point2) {
			super(point1, point2);
		}

		public Standard(Location point1, Location point2, HUID id) {
			super(point1, point2, id);
		}

	}

	public static class Spawn extends Region {

		private Location loc;

		public Spawn(Region cuboid) {
			super(cuboid);
		}

		public Spawn(Location point1, Location point2) {
			super(point1, point2);
		}

		public Spawn(Location point1, Location point2, HUID id) {
			super(point1, point2, id);
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
						}
					}
				}
			}
			return list;
		}

		@Override
		public void save() throws IOException {
			if (this.getStartingPoint() != null && this.getEndingPoint() != null && this.location() != null) {
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".pos1", this.getStartingPoint());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".pos2", this.getEndingPoint());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".start", location());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".owner", this.getOwner().getUniqueId().toString());
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".members", getMembers().stream().map(OfflinePlayer::getUniqueId).map(UUID::toString).collect(Collectors.toList()));
				DATA.getConfig().set("Markers.spawn." + getId().toString() + ".flags", FLAGS.stream().map(Enum::name).collect(Collectors.toList()));
				DATA.saveConfig();
			} else
				throw new IOException("One or more locations were found null during the saving process.");
		}

	}

	public enum Flag {
		BUILDING, PVP, JOIN_MSG, LEAVE_MSG;

		public Object factor() {
			switch (this) {
				case PVP:
				case BUILDING:
					return true;
				case JOIN_MSG:
				case LEAVE_MSG:
				default:
					throw new IllegalStateException();
			}
		}

	}

}