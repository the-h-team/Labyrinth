package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.event.*;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.task.Schedule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class RegionServicesManager {

	private final LinkedList<RegionService> SERVICES = new LinkedList<>();

	private final Cuboid.FlagManager flagManager;

	public RegionServicesManager() {
		this.flagManager = new Cuboid.FlagManager();
	}

	public static RegionServicesManager getInstance() {
		return Bukkit.getServicesManager().load(RegionServicesManager.class);
	}

	public boolean isRegistered(Cuboid.Flag flag) {
		return getFlagManager().getFlags().stream().anyMatch(f -> f.getId().equals(flag.getId()));
	}

	public boolean unregister(Cuboid.Flag flag) {
		for (Region r : Region.cache().list()) {
			r.getFlags().forEach(f -> {
				if (f.getId().equals(flag.getId())) {
					Schedule.sync(() -> r.removeFlag(f)).run();
				}
			});
		}
		return getFlagManager().getFlags().removeIf(f -> f.getId().equals(flag.getId()));
	}

	public boolean register(Cuboid.Flag flag) {
		if (getFlagManager().getFlags().stream().noneMatch(f -> f.getId().equals(flag.getId()))) {
			getFlagManager().getFlags().add(flag);
			return true;
		}
		return false;
	}

	public boolean load(Cuboid.Flag flag) {
		Bukkit.getPluginManager().registerEvents(flag, LabyrinthProvider.getInstance().getPluginInstance());
		return getFlagManager().getFlags().add(flag);
	}

	public boolean load(Vent.Subscription<?> subscription) {
		Vent.subscribe(subscription);
		return true;
	}

	@Deprecated
	public boolean load(RegionService service) {
		Bukkit.getPluginManager().registerEvents(service, LabyrinthProvider.getInstance().getPluginInstance());
		return SERVICES.add(service);
	}

	@Deprecated
	public boolean unload(RegionService service) {
		HandlerList.unregisterAll(service);
		return SERVICES.remove(service);
	}

	public Cuboid.FlagManager getFlagManager() {
		return flagManager;
	}

	public static final class Initializer {

		public static void start(LabyrinthAPI instance) {

			RegionServicesManager servicesManager = new RegionServicesManager();
			final Plugin pluginInstance = instance.getPluginInstance();
			Bukkit.getServicesManager().register(RegionServicesManager.class, servicesManager, pluginInstance, ServicePriority.Normal);

			Schedule.sync(() -> {
				RegionServicesManager manager = getInstance();
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
							Schedule.sync(() -> spawn.setPlugin(Bukkit.getPluginManager().getPlugin(Region.DATA.getConfig().getString("Markers.spawn." + id + ".plugin")))).run();
							spawn.addMember(members.stream().map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new));
							spawn.addFlag(flags.toArray(new Region.Flag[0]));
							if (!spawn.load()) {
								instance.getLogger().warning("- A region under the name '" + spawn.getId() + "' has failed to attach properly.");
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
							Schedule.sync(() -> region.setPlugin(Bukkit.getPluginManager().getPlugin(Region.DATA.getConfig().getString("Markers.region." + id + ".plugin")))).run();
							region.addMember(members.stream().map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new));
							region.addFlag(flags.toArray(new Region.Flag[0]));
							if (!region.load()) {
								instance.getLogger().warning("- A region under the name '" + region.getId() + "' has failed to attach properly.");
							}
						}
					}
				}
			}).waitReal(2);

			Schedule.sync(() -> {


				for (Region.Loading load : Region.loading().list()) {
					Region.Standard result = new Region.Standard(load);
					result.setPassthrough(load.isPassthrough());
					if (!result.load()) {
						instance.getLogger().warning("- A pre-loaded region under the name '" + result.getId() + "' has failed to attach properly.");
					}
					load.remove();
				}

				for (Region.Spawning spawn : Region.spawning().list()) {
					Region.Spawn result = new Region.Spawn(spawn);
					result.setPassthrough(spawn.isPassthrough());
					result.setLocation(spawn.location());
					if (!result.load()) {
						instance.getLogger().warning("- A pre-loaded region under the name '" + result.getId() + "' has failed to attach properly.");
					}
					spawn.remove();
				}

			}).waitReal(5);

			Schedule.async(() -> Bukkit.getOnlinePlayers().forEach(p -> {

				if (Cuboid.Selection.contains(p)) {

					final Cuboid.Selection selection = Cuboid.Selection.source(p);

					Schedule.sync(() -> new Vent.Call<>(new CuboidSelectionEvent(selection)).run()).run();

				}

			})).repeat(5, 2 * 20);

			Vent.subscribe(new Vent.Subscription<>(DefaultEvent.BlockBreak.class, pluginInstance, Vent.Priority.HIGH, (e, subscription) -> {

				Optional<Region> r = CompletableFuture.supplyAsync(() -> Region.match(e.getBlock().getLocation())).join();
				if (r.isPresent()) {
					RegionDestroyEvent event = new Vent.Call<>(new RegionDestroyEvent(e.getPlayer(), r.get(), e.getBlock())).run();
					if (event.isCancelled()) {
						e.setCancelled(true);
					}
				}

			}));

			Vent.subscribe(new Vent.Subscription<>(DefaultEvent.BlockPlace.class, pluginInstance, Vent.Priority.HIGH, (e, subscription) -> {

				Optional<Region> r = CompletableFuture.supplyAsync(() -> Region.match(e.getBlock().getLocation())).join();
				if (r.isPresent()) {
					RegionBuildEvent event = new Vent.Call<>(new RegionBuildEvent(e.getPlayer(), r.get(), e.getBlock())).run();
					if (event.isCancelled()) {
						e.setCancelled(true);
					}
				}

			}));

			Vent.subscribe(new Vent.Subscription<>(DefaultEvent.Join.class, "region-spawn", pluginInstance, Vent.Priority.MEDIUM, (event, subscription) -> {

				Region.Resident r = Region.Resident.get(event.getPlayer());
				if (!event.getPlayer().hasPlayedBefore()) {
					Schedule.sync(() -> {
						if (Region.spawn().isPresent()) {
							event.getPlayer().teleport(Region.spawn().get().location());
							r.setSpawnTagged(true);
							r.setPastSpawn(false);
						}
					}).wait(2);
				} else {
					r.setSpawnTagged(false);
				}

			}));

			Vent.subscribe(new Vent.Subscription<>(DefaultEvent.Interact.class, "region-interact", pluginInstance, Vent.Priority.MEDIUM, (e, subscription) -> {

				if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
					Optional<Region> r = CompletableFuture.supplyAsync(() -> Region.match(e.getBlock().get().getLocation())).join();
					if (r.isPresent()) {
						RegionInteractionEvent event = new Vent.Call<>(new RegionInteractionEvent(e.getPlayer(), r.get(), e.getBlock().get(), RegionInteractionEvent.ClickType.LEFT)).run();
						if (event.isCancelled()) {
							e.setCancelled(true);
						}
					}

					if (e.getItem() == null) {
						return;
					}

					if (!e.getPlayer().hasPermission("labyrinth.selection"))
						return;

					boolean isOld = instance.isLegacy();

					Material mat = Items.getMaterial("WOODEN_AXE");

					if (isOld) {
						mat = Items.getMaterial("WOOD_AXE");
					}

					if (e.getItem().getType() == mat) {
						Cuboid.Selection selection = Cuboid.Selection.source(e.getPlayer());
						if (e.getResult() != Event.Result.DENY) {
							e.setResult(Event.Result.DENY);
						}

						selection.setPos1(e.getBlock().get().getLocation());

						new Vent.Call<>(new CuboidCreationEvent(selection)).run();

					}
				}
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Optional<Region> r = CompletableFuture.supplyAsync(() -> Region.match(e.getBlock().get().getLocation())).join();
					if (r.isPresent()) {
						RegionInteractionEvent event = new Vent.Call<>(new RegionInteractionEvent(e.getPlayer(), r.get(), e.getBlock().get(), RegionInteractionEvent.ClickType.RIGHT)).run();
						if (event.isCancelled()) {
							e.setCancelled(true);
						}
					}

					if (e.getItem() == null)
						return;

					if (!e.getPlayer().hasPermission("labyrinth.selection"))
						return;

					boolean isOld = LabyrinthProvider.getInstance().isLegacy();

					Material mat = Items.getMaterial("WOODEN_AXE");

					if (isOld) {
						mat = Items.getMaterial("WOOD_AXE");
					}

					if (e.getItem().getType() == mat) {
						Cuboid.Selection selection = Cuboid.Selection.source(e.getPlayer());
						if (e.getResult() != Event.Result.DENY) {
							e.setResult(Event.Result.DENY);
						}

						selection.setPos2(e.getBlock().get().getLocation());

						new Vent.Call<>(new CuboidCreationEvent(selection)).run();
					}
				}

			}));

			Vent.subscribe(new Vent.Subscription<>(DefaultEvent.PlayerDamagePlayer.class, "region-pvp", pluginInstance, Vent.Priority.MEDIUM, (event, subscription) -> {

				Player p = event.getPlayer();
				Player target = event.getVictim();

				Message msg = Message.form(p);
				Region.Resident r = Region.Resident.get(target);
				if (event.isPhysical()) {

					if (r.getRegion().isPresent()) {
						Region region = r.getRegion().get();

						RegionPVPEvent e = new Vent.Call<>(new RegionPVPEvent(p, target, region)).run();

						if (e.isCancelled()) {
							event.setCancelled(true);
							return;
						}

						if (region instanceof Region.Spawn) {

							Region.Resident o = Region.Resident.get(p);

							Region.Resident t = Region.Resident.get(target);

							if (t.isSpawnTagged()) {
								event.setCancelled(true);
								msg.send("&3&o" + target.getDisplayName() + " &bis protected, you can't hurt them.");
							}
							if (o.isSpawnTagged()) {
								if (!t.isSpawnTagged()) {
									o.setSpawnTagged(false);
									msg.send("&c&oRemoving protection under offensive maneuvers. &7" + target.getDisplayName() + "&c&o isn't protected.");
								}
							}
						}
					}
				} else {

					if (r.getRegion().isPresent()) {
						Region region = r.getRegion().get();

						RegionPVPEvent e = new Vent.Call<>(new RegionPVPEvent(p, target, region)).run();

						if (e.isCancelled()) {
							event.setCancelled(true);
							return;
						}

						if (region instanceof Region.Spawn) {

							Region.Resident o = Region.Resident.get(p);

							Region.Resident t = Region.Resident.get(target);

							if (t.isSpawnTagged()) {
								event.setCancelled(true);
								msg.send("&3&o" + target.getDisplayName() + " &bis protected, you can't hurt them.");
							}
							if (o.isSpawnTagged()) {
								if (t.isSpawnTagged()) {
									o.setSpawnTagged(false);
									msg.send("&c&oRemoving protection under offensive maneuvers. &7" + target.getDisplayName() + "&c&o isn't protected.");
								}
							}
						}
					}

				}

			}));


		}

	}
}
