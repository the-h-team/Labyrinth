package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.data.AdvancedHook;
import com.github.sanctum.labyrinth.data.DefaultProvision;
import com.github.sanctum.labyrinth.data.EconomyProvision;
import com.github.sanctum.labyrinth.data.Region;
import com.github.sanctum.labyrinth.data.RegionFlag;
import com.github.sanctum.labyrinth.data.RegionServicesManager;
import com.github.sanctum.labyrinth.data.VaultHook;
import com.github.sanctum.labyrinth.data.container.DataComponent;
import com.github.sanctum.labyrinth.data.container.DataContainer;
import com.github.sanctum.labyrinth.data.container.DataParser;
import com.github.sanctum.labyrinth.event.CuboidController;
import com.github.sanctum.labyrinth.event.CuboidSelectionEvent;
import com.github.sanctum.labyrinth.event.EventBuilder;
import com.github.sanctum.labyrinth.formatting.string.WrappedComponent;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Item;
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
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
		Bukkit.getServicesManager().register(RegionServicesManager.class, servicesManager, this, ServicePriority.Normal);
		EconomyProvision provision = new DefaultProvision();
		EventBuilder.register(new CuboidController());
		Bukkit.getServicesManager().register(EconomyProvision.class, provision, this, ServicePriority.Normal);
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
		getLogger().info("- Process ignored. No directory found to process.");
		getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		Schedule.async(() -> Arrays.stream(Bukkit.getOfflinePlayers()).forEach(SkullItem.Head::search)).run();
		run(() -> new VaultHook(this)).applyAfter(() -> new AdvancedHook(this)).wait(2);
		run(() -> CommandUtils.initialize(Labyrinth.this)).run();

		run(() -> {
			RegionServicesManager manager = RegionServicesManager.getInstance();
			manager.load(manager.getFlagManager().getDefault(Cuboid.FlagManager.FlagType.BREAK));
			manager.load(manager.getFlagManager().getDefault(Cuboid.FlagManager.FlagType.BUILD));
			manager.load(manager.getFlagManager().getDefault(Cuboid.FlagManager.FlagType.PVP));
			if (Region.DATA.exists()) {
				if (Region.DATA.getConfig().isConfigurationSection("Markers.spawn")) {
					for (String id : Region.DATA.getConfig().getConfigurationSection("Markers.spawn").getKeys(false)) {
						Location o = Region.DATA.getConfig().getLocation("Markers.spawn." + id + ".pos1");

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
						spawn.setPassthrough(Region.DATA.getConfig().getBoolean("Markers.spawn." + id + ".pass"));
						spawn.setLocation(s);
						spawn.setOwner(owner);
						if (Region.DATA.getConfig().getString("Markers.spawn." + id + ".name") != null) {
							spawn.setName(Region.DATA.getConfig().getString("Markers.spawn." + id + ".name"));
						}
						run(() -> spawn.setPlugin(Bukkit.getPluginManager().getPlugin(Region.DATA.getConfig().getString("Markers.spawn." + id + ".plugin")))).run();
						spawn.addMember(members.stream().map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new));
						spawn.addFlag(flags.toArray(new Region.Flag[0]));
						if (!spawn.load()) {
							getLogger().warning("- A region under the name '" + spawn.getId() + "' has failed to attach properly.");
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
						region.setPassthrough(Region.DATA.getConfig().getBoolean("Markers.region." + id + ".pass"));
						region.setOwner(owner);
						if (Region.DATA.getConfig().getString("Markers.region." + id + ".name") != null) {
							region.setName(Region.DATA.getConfig().getString("Markers.region." + id + ".name"));
						}
						region.setPassthrough(Region.DATA.getConfig().getBoolean("Markers.region." + id + ".pass"));
						run(() -> region.setPlugin(Bukkit.getPluginManager().getPlugin(Region.DATA.getConfig().getString("Markers.region." + id + ".plugin")))).run();
						region.addMember(members.stream().map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new));
						region.addFlag(flags.toArray(new Region.Flag[0]));
						if (!region.load()) {
							getLogger().warning("- A region under the name '" + region.getId() + "' has failed to attach properly.");
						}
					}
				}
			}
		}).waitReal(2);

		run(() -> {


			for (Region.Loading load : Region.loading().list()) {
				Region.Standard result = new Region.Standard(load);
				result.setPassthrough(load.isPassthrough());
				if (!result.load()) {
					getLogger().warning("- A pre-loaded region under the name '" + result.getId() + "' has failed to attach properly.");
				}
				load.remove();
			}

			for (Region.Spawning spawn : Region.spawning().list()) {
				Region.Spawn result = new Region.Spawn(spawn);
				result.setPassthrough(spawn.isPassthrough());
				result.setLocation(spawn.location());
				if (!result.load()) {
					getLogger().warning("- A pre-loaded region under the name '" + result.getId() + "' has failed to attach properly.");
				}
				spawn.remove();
			}

		}).waitReal(5);

		Schedule.async(() -> Bukkit.getOnlinePlayers().forEach(p -> {

			if (Cuboid.Selection.contains(p)) {

				final Cuboid.Selection selection = Cuboid.Selection.source(p);

				Schedule.sync(() -> {
					CuboidSelectionEvent event = new CuboidSelectionEvent(selection);
					getServer().getPluginManager().callEvent(event);
				}).run();

			}

		})).repeat(5, 15);
	}

	@Override
	public void onDisable() {

		for (DataComponent component : DataParser.getDataComponents()) {
			for (String key : component.keySet()) {
				component.save(key);
			}
		}

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

	public static DataComponent getPersistentData(NamespacedKey key) {
		return DataParser.test(key);
	}

	public static Plugin getInstance() {
		return instance;
	}

	private Synchronous run(Applicable applicable) {
		return Schedule.sync(applicable);
	}


}
