package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.event.CuboidCreationEvent;
import com.github.sanctum.labyrinth.event.CuboidSelectionEvent;
import com.github.sanctum.labyrinth.event.RegionBuildEvent;
import com.github.sanctum.labyrinth.event.RegionDestroyEvent;
import com.github.sanctum.labyrinth.event.RegionInteractionEvent;
import com.github.sanctum.labyrinth.event.RegionPVPEvent;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.ServicePriority;

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
		Bukkit.getPluginManager().registerEvents(flag, Labyrinth.getInstance());
		return SERVICES.add(flag) && getFlagManager().getFlags().add(flag);
	}

	public boolean load(RegionService service) {
		Bukkit.getPluginManager().registerEvents(service, Labyrinth.getInstance());
		return SERVICES.add(service);
	}

	public boolean unload(RegionService service) {
		HandlerList.unregisterAll(service);
		return SERVICES.remove(service);
	}

	public Cuboid.FlagManager getFlagManager() {
		return flagManager;
	}

	public static final class Initializer {

		public static void start(Labyrinth instance) {

			RegionServicesManager servicesManager = new RegionServicesManager();
			Bukkit.getServicesManager().register(RegionServicesManager.class, servicesManager, instance, ServicePriority.Normal);

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

					Schedule.sync(() -> {
						CuboidSelectionEvent event = new CuboidSelectionEvent(selection);
						instance.getServer().getPluginManager().callEvent(event);
					}).run();

				}

			})).repeat(5, 2 * 20);

			Vent.subscribe(new Vent.Subscription<>(DefaultEvent.BlockBreak.class, instance, Vent.Priority.HIGH, (e, subscription) -> {

				Optional<Region> r = CompletableFuture.supplyAsync(() -> Region.match(e.getBlock().getLocation())).join();
				if (r.isPresent()) {
					RegionDestroyEvent event = new RegionDestroyEvent(e.getPlayer(), r.get(), e.getBlock());
					Bukkit.getPluginManager().callEvent(event);
					if (event.isCancelled()) {
						e.setCancelled(true);
					}
				}

			}));

			Vent.subscribe(new Vent.Subscription<>(DefaultEvent.BlockPlace.class, instance, Vent.Priority.HIGH, (e, subscription) -> {

				Optional<Region> r = CompletableFuture.supplyAsync(() -> Region.match(e.getBlock().getLocation())).join();
				if (r.isPresent()) {
					RegionBuildEvent event = new RegionBuildEvent(e.getPlayer(), r.get(), e.getBlock());
					Bukkit.getPluginManager().callEvent(event);
					if (event.isCancelled()) {
						e.setCancelled(true);
					}
				}

			}));

			Vent.subscribe(new Vent.Subscription<>(DefaultEvent.Join.class, "region-spawn", instance, Vent.Priority.MEDIUM, (event, subscription) -> {

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

			Vent.subscribe(new Vent.Subscription<>(DefaultEvent.Interact.class, "region-interact", instance, Vent.Priority.MEDIUM, (e, subscription) -> {

				if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
					Optional<Region> r = CompletableFuture.supplyAsync(() -> Region.match(e.getBlock().get().getLocation())).join();
					if (r.isPresent()) {
						RegionInteractionEvent event = new RegionInteractionEvent(e.getPlayer(), r.get(), e.getBlock().get(), RegionInteractionEvent.ClickType.LEFT);
						Bukkit.getPluginManager().callEvent(event);
						if (event.isCancelled()) {
							e.setCancelled(true);
						}
					}

					if (e.getItem() == null)
						return;

					if (!e.getPlayer().hasPermission("labyrinth.selection"))
						return;

					boolean isNew = Arrays.stream(Material.values()).anyMatch(m -> m.name().equals("WOODEN_AXE"));

					Material mat = Items.getMaterial("WOODEN_AXE");

					if (!isNew) {
						mat = Items.getMaterial("WOOD_AXE");
					}

					if (e.getItem().getType() == mat) {
						Cuboid.Selection selection = Cuboid.Selection.source(e.getPlayer());
						if (e.getResult() != Event.Result.DENY) {
							e.setResult(Event.Result.DENY);
						}

						selection.setPos1(e.getBlock().get().getLocation());

						CuboidCreationEvent event = new CuboidCreationEvent(selection);
						Bukkit.getPluginManager().callEvent(event);

					}
				}
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Optional<Region> r = CompletableFuture.supplyAsync(() -> Region.match(e.getBlock().get().getLocation())).join();
					if (r.isPresent()) {
						RegionInteractionEvent event = new RegionInteractionEvent(e.getPlayer(), r.get(), e.getBlock().get(), RegionInteractionEvent.ClickType.RIGHT);
						Bukkit.getPluginManager().callEvent(event);
						if (event.isCancelled()) {
							e.setCancelled(true);
						}
					}

					if (e.getItem() == null)
						return;

					if (!e.getPlayer().hasPermission("labyrinth.selection"))
						return;

					boolean isNew = Arrays.stream(Material.values()).anyMatch(m -> m.name().equals("WOODEN_AXE"));

					Material mat = Items.getMaterial("WOODEN_AXE");

					if (!isNew) {
						mat = Items.getMaterial("WOOD_AXE");
					}

					if (e.getItem().getType() == mat) {
						Cuboid.Selection selection = Cuboid.Selection.source(e.getPlayer());
						if (e.getResult() != Event.Result.DENY) {
							e.setResult(Event.Result.DENY);
						}

						selection.setPos2(e.getBlock().get().getLocation());

						CuboidCreationEvent event = new CuboidCreationEvent(selection);
						Bukkit.getPluginManager().callEvent(event);
					}
				}

			}));

			Vent.subscribe(new Vent.Subscription<>(DefaultEvent.PlayerDamagePlayer.class, "region-pvp", instance, Vent.Priority.MEDIUM, (event, subscription) -> {

				Player p = event.getPlayer();
				Player target = event.getVictim();

				Message msg = Message.form(p);
				Region.Resident r = Region.Resident.get(target);
				if (event.isPhysical()) {

					if (r.getRegion().isPresent()) {
						Region region = r.getRegion().get();

						RegionPVPEvent e = new RegionPVPEvent(p, target, region);
						Bukkit.getPluginManager().callEvent(e);

						if (e.isCancelled()) {
							event.setCancelled(true);
							return;
						}

						if (region instanceof Region.Spawn) {

							Region.Spawn spawn = (Region.Spawn) region;

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

						RegionPVPEvent e = new RegionPVPEvent(p, target, region);
						Bukkit.getPluginManager().callEvent(e);

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
