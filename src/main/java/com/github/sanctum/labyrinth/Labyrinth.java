package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.data.AdvancedHook;
import com.github.sanctum.labyrinth.data.BoundaryAction;
import com.github.sanctum.labyrinth.data.DefaultProvision;
import com.github.sanctum.labyrinth.data.EconomyProvision;
import com.github.sanctum.labyrinth.data.Region;
import com.github.sanctum.labyrinth.data.RegionFlag;
import com.github.sanctum.labyrinth.data.RegionServicesManager;
import com.github.sanctum.labyrinth.data.VaultHook;
import com.github.sanctum.labyrinth.data.container.DataContainer;
import com.github.sanctum.labyrinth.event.CuboidController;
import com.github.sanctum.labyrinth.event.EventBuilder;
import com.github.sanctum.labyrinth.formatting.string.WrappedComponent;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.SkullItem;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.task.Synchronous;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class Labyrinth extends JavaPlugin implements Listener {

	public static final LinkedList<Cooldown> COOLDOWNS = new LinkedList<>();
	public static final LinkedList<WrappedComponent> COMPONENTS = new LinkedList<>();
	public static final ConcurrentLinkedQueue<Integer> TASKS = new ConcurrentLinkedQueue<>();
	private static Labyrinth instance;

	@Override
	public void onEnable() {
		instance = this;
		RegionServicesManager servicesManager = new RegionServicesManager();
		EconomyProvision provision = new DefaultProvision();
		Bukkit.getServicesManager().register(EconomyProvision.class, provision, this, ServicePriority.Normal);
		Bukkit.getServicesManager().register(RegionServicesManager.class, servicesManager, this, ServicePriority.Normal);
		getLogger().info("- Registered factory implementation, " + provision.getImplementation());
		boolean success;
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		getLogger().info("Labyrinth (C) 2021, Open-source spigot development tool.");
		getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		try {
			DataContainer.querySaved();
			success = true;
		} catch (NullPointerException e) {
			getLogger().info("- Process ignored. No directory found to process.");
			getLogger().info("- Store a new instance of data for query to take effect on enable.");
			getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			success = false;
		}
		if (success) {
			if (DataContainer.get().length == 0) {
				getLogger().info("- Process ignored. No data found to process.");
			}
			getLogger().info("- Query success! All found meta cached. (" + DataContainer.get().length + ")");
		} else {
			getLogger().info("- Query failed! (SEE ABOVE FOR INFO)");
		}
		getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		Schedule.async(() -> {
			final boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
			final Material type = Items.getMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
			Arrays.stream(Bukkit.getOfflinePlayers()).forEach(p -> {
				ItemStack item = new ItemStack(type, 1);
				if (!isNew) {
					item.setDurability((short) 3);
				}
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				assert meta != null;
				meta.setOwningPlayer(p);
				item.setItemMeta(meta);
				new SkullItem(p.getUniqueId().toString(), item);
			});
		}).run();
		run(() -> new VaultHook(this)).applyAfter(() -> new AdvancedHook(this)).wait(2);
		run(() -> CommandUtils.initialize(Labyrinth.this)).run();
		EventBuilder.register(new CuboidController());

		Schedule.sync(() -> Bukkit.getOnlinePlayers().forEach(p -> {

			if (Cuboid.Selection.contains(p)) {

				Cuboid.Selection selection = Cuboid.Selection.source(p);

				if (selection.getPos1() != null && selection.getPos2() == null) {
					Cuboid.Boundary cube = new Region.Boundary(selection.getPos1().getBlockX(), selection.getPos1().getBlockX(), selection.getPos1().getBlockY(), selection.getPos1().getBlockY(), selection.getPos1().getBlockZ(), selection.getPos1().getBlockZ()).target(p);
					cube.deploy(action -> action.getPlayer().spawnParticle(org.bukkit.Particle.REDSTONE, action.getX(), action.getY(), action.getZ(), 1, new Particle.DustOptions(Cuboid.Boundary.Particle.GREEN.toColor(), 2)));
				}
				if (selection.getPos2() != null && selection.getPos1() == null) {
					Cuboid.Boundary cube = new Region.Boundary(selection.getPos2().getBlockX(), selection.getPos2().getBlockX(), selection.getPos2().getBlockY(), selection.getPos2().getBlockY(), selection.getPos2().getBlockZ(), selection.getPos2().getBlockZ()).target(p);
					cube.deploy(action -> action.getPlayer().spawnParticle(org.bukkit.Particle.REDSTONE, action.getX(), action.getY(), action.getZ(), 1, new Particle.DustOptions(Cuboid.Boundary.Particle.GREEN.toColor(), 2)));
				}
				if (selection.getPos1() != null && selection.getPos2() != null) {
					Cuboid.Boundary cube = new Region.Boundary(Math.max(selection.getPos1().getBlockX(), selection.getPos2().getBlockX()) + 0.5, Math.min(selection.getPos1().getBlockX(), selection.getPos2().getBlockX()) + 0.5, Math.max(selection.getPos1().getBlockY(), selection.getPos2().getBlockY()) + 0.5, Math.min(selection.getPos1().getBlockY(), selection.getPos2().getBlockY()) + 0.5, Math.max(selection.getPos1().getBlockZ(), selection.getPos2().getBlockZ()) + 0.5, Math.min(selection.getPos1().getBlockZ(), selection.getPos2().getBlockZ()) + 0.5).target(p);
					cube.deploy(BoundaryAction::box);
				}
			}

		})).repeatReal(0, 5);

		run(() -> {
			RegionServicesManager manager = RegionServicesManager.getInstance();
			manager.load(manager.getFlagManager().getDefault(Cuboid.FlagManager.FlagType.BREAK));
			manager.load(manager.getFlagManager().getDefault(Cuboid.FlagManager.FlagType.BUILD));
			manager.load(manager.getFlagManager().getDefault(Cuboid.FlagManager.FlagType.PVP));
			if (Region.DATA.exists()) {
				if (Region.DATA.getConfig().isConfigurationSection("Markers.spawn")) {
					for (String id : Region.DATA.getConfig().getConfigurationSection("Markers.spawn").getKeys(false)) {
						Location o = Region.DATA.getConfig().getLocation("Markers.spawn." + id + ".pos1");

						if (o.getWorld() == null) {
							throw new IllegalStateException("World is null??");
						}

						Location t = Region.DATA.getConfig().getLocation("Markers.spawn." + id + ".pos2");
						Location s = Region.DATA.getConfig().getLocation("Markers.spawn." + id + ".start");
						HUID d = HUID.fromString(id);
						UUID owner = UUID.fromString(Region.DATA.getConfig().getString("Markers.spawn." + id + ".owner"));
						List<UUID> members = Region.DATA.getConfig().getStringList("Markers.spawn." + id + ".members").stream().map(UUID::fromString).collect(Collectors.toList());
						List<Region.Flag> flags = new ArrayList<>();
						if (Region.DATA.getConfig().isConfigurationSection("Markers.spawn." + id + ".flags")) {
							for (String flag : Region.DATA.getConfig().getConfigurationSection("Markers.spawn." + id + ".flags").getKeys(false)) {
								Cuboid.Flag f = manager.getFlagManager().getFlag(flag).orElse(null);
								if (f != null) {
									RegionFlag copy = new RegionFlag(f);
									copy.setMessage(Region.DATA.getConfig().getString("Markers.spawn." + id + ".flags." + flag + ".message"));
									copy.setAllowed(Region.DATA.getConfig().getBoolean("Markers.spawn." + id + ".flags." + flag + ".allowed"));
									flags.add(copy);
								}
							}
						}
						Region.Spawning spawn = new Region.Spawning(o, t, d);
						spawn.setLocation(s);
						spawn.setOwner(owner);
						run(() -> spawn.setPlugin(getServer().getPluginManager().getPlugin(Region.DATA.getConfig().getString("Markers.spawn." + id + ".plugin")))).run();
						spawn.addMember(members.stream().map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new));
						spawn.addFlag(flags.toArray(new Region.Flag[0]));
						if (!spawn.load()) {
							getLogger().warning("- A region under the name '" + spawn.getId() + "' has failed to load properly.");
						}
					}
				}
				if (Region.DATA.getConfig().isConfigurationSection("Markers.region")) {
					for (String id : Region.DATA.getConfig().getConfigurationSection("Markers.region").getKeys(false)) {
						Location o = Region.DATA.getConfig().getLocation("Markers.region." + id + ".pos1");
						Location t = Region.DATA.getConfig().getLocation("Markers.region." + id + ".pos2");
						HUID d = HUID.fromString(id);
						UUID owner = UUID.fromString(Region.DATA.getConfig().getString("Markers.region." + id + ".owner"));
						List<UUID> members = Region.DATA.getConfig().getStringList("Markers.region." + id + ".members").stream().map(UUID::fromString).collect(Collectors.toList());
						List<Region.Flag> flags = new ArrayList<>();
						if (Region.DATA.getConfig().isConfigurationSection("Markers.region." + id + ".flags")) {
							for (String flag : Region.DATA.getConfig().getConfigurationSection("Markers.region." + id + ".flags").getKeys(false)) {
								Cuboid.Flag f = manager.getFlagManager().getFlag(flag).orElse(null);
								if (f != null) {
									RegionFlag copy = new RegionFlag(f);
									copy.setMessage(Region.DATA.getConfig().getString("Markers.region." + id + ".flags." + flag + ".message"));
									copy.setAllowed(Region.DATA.getConfig().getBoolean("Markers.region." + id + ".flags." + flag + ".allowed"));
									flags.add(copy);
								}
							}
						}
						Region.Loading region = new Region.Loading(o, t, d);
						region.setOwner(owner);
						run(() -> region.setPlugin(getServer().getPluginManager().getPlugin(Region.DATA.getConfig().getString("Markers.region." + id + ".plugin")))).run();
						region.addMember(members.stream().map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new));
						region.addFlag(flags.toArray(new Region.Flag[0]));
						if (!region.load()) {
							getLogger().warning("- A region under the name '" + region.getId() + "' has failed to load properly.");
						}
					}
				}
			}
		}).wait(2);

		run(() -> {
			for (Region.Loading load : Region.loading().list()) {
				Region.Standard result = new Region.Standard(load);
				if (!result.load()) {
					getLogger().warning("- A pre-loaded region under the name '" + result.getId() + "' has failed to load properly.");
				}
				load.remove();
			}

			for (Region.Spawning spawn : Region.spawning().list()) {
				Region.Spawn result = new Region.Spawn(spawn);
				result.setLocation(spawn.location());
				if (!result.load()) {
					getLogger().warning("- A pre-loaded region under the name '" + result.getId() + "' has failed to load properly.");
				}
				spawn.remove();
			}
		}).wait(5);
	}

	@Override
	public void onDisable() {
		for (int id : TASKS) {
			getServer().getScheduler().cancelTask(id);
		}
		try {
			Thread.sleep(1);
		} catch (InterruptedException ignored) {
		}
		SkullItem.getLog().clear();
		if (Item.getCache().size() > 0) {
			for (Item i : Item.getCache()) {
				Item.removeEntry(i);
			}
		}

	}

	@EventHandler
	public void onCommandNote(PlayerCommandPreprocessEvent e) {
		for (WrappedComponent component : COMPONENTS) {
			if (StringUtils.use(e.getMessage().replace("/", "")).containsIgnoreCase(component.toString())) {
				run(() -> component.action().apply()).run();
				if (!component.isMarked()) {
					component.setMarked(true);
					run(component::remove).waitReal(200);
				}
				e.setCancelled(true);
			}
		}
	}

	public static Plugin getInstance() {
		return instance;
	}

	private Synchronous run(Applicable applicable) {
		return Schedule.sync(applicable);
	}


}
