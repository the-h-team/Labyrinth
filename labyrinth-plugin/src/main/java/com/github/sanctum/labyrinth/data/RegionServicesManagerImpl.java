package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.container.ImmutableLabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import com.github.sanctum.labyrinth.event.CuboidSelectEvent;
import com.github.sanctum.labyrinth.event.RegionBuildEvent;
import com.github.sanctum.labyrinth.event.RegionDestroyEvent;
import com.github.sanctum.labyrinth.event.RegionInteractionEvent;
import com.github.sanctum.labyrinth.event.RegionPVPEvent;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.Mailer;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

/**
 * Default {@link RegionServicesManager} implementation.
 */
public final class RegionServicesManagerImpl extends RegionServicesManager {

	final LabyrinthCollection<Region> cache = new LabyrinthList<>();
	final Cuboid.FlagManager flagManager = new Cuboid.FlagManager(this);

	public static void initialize(Labyrinth instance) {

		RegionServicesManager servicesManager = new RegionServicesManagerImpl();
		Bukkit.getServicesManager().register(RegionServicesManager.class, servicesManager, instance, ServicePriority.Normal);

		instance.getEventMap().subscribe(new Vent.Subscription<>(DefaultEvent.BlockBreak.class, instance, Vent.Priority.HIGH, (e, subscription) -> {

			Optional<Region> r = CompletableFuture.supplyAsync(() -> Optional.ofNullable(getInstance().get(e.getBlock().getLocation()))).join();
			if (r.isPresent()) {
				RegionDestroyEvent event = new Vent.Call<>(new RegionDestroyEvent(e.getPlayer(), r.get(), e.getBlock())).run();
				if (event.isCancelled()) {
					e.setCancelled(true);
				}
			}

		}));

		instance.getEventMap().subscribe(new Vent.Subscription<>(DefaultEvent.BlockPlace.class, instance, Vent.Priority.HIGH, (e, subscription) -> {

			Optional<Region> r = CompletableFuture.supplyAsync(() -> Optional.ofNullable(getInstance().get(e.getBlock().getLocation()))).join();
			if (r.isPresent()) {
				RegionBuildEvent event = new Vent.Call<>(new RegionBuildEvent(e.getPlayer(), r.get(), e.getBlock())).run();
				if (event.isCancelled()) {
					e.setCancelled(true);
				}
			}

		}));

		instance.getEventMap().subscribe(new Vent.Subscription<>(DefaultEvent.Join.class, "region-spawn", instance, Vent.Priority.MEDIUM, (event, subscription) -> {

			Region.Resident r = Region.Resident.get(event.getPlayer());
			if (!event.getPlayer().hasPlayedBefore()) {
				TaskScheduler.of(() -> {
					Region region = getInstance().get("spawn");
					if (region != null) {
						r.setSpawnTagged(true);
						r.setPastSpawn(false);
					}
				}).scheduleLater(2);
			} else {
				r.setSpawnTagged(false);
			}

		}));

		instance.getEventMap().subscribe(new Vent.Subscription<>(DefaultEvent.Interact.class, "region-interact", instance, Vent.Priority.MEDIUM, (e, subscription) -> {

			if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				Optional<Region> r = CompletableFuture.supplyAsync(() -> e.getBlock().map(Block::getLocation).flatMap(location -> Optional.ofNullable(getInstance().get(location)))).join();
				if (r.isPresent()) {
					// TODO: rewrite the optional#get call to safely obtain the value
					//noinspection OptionalGetWithoutIsPresent
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

				Cuboid.Selection selection = Cuboid.Selection.source(e.getPlayer());

				if (e.getItem().getType() == selection.getWand().getType()) {
					if (e.getResult() != Event.Result.DENY) {
						e.setResult(Event.Result.DENY);
					}

					// TODO: decide how to handle optional (map setPos1?)
					//noinspection OptionalGetWithoutIsPresent
					selection.setPos1(e.getBlock().get().getLocation());

					new Vent.Call<>(new CuboidSelectEvent(selection) {
					}).run();

				}
			}
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Optional<Region> r = CompletableFuture.supplyAsync(() -> e.getBlock().map(Block::getLocation).flatMap(location -> Optional.ofNullable(getInstance().get(location)))).join();
				if (r.isPresent()) {
					// TODO: rewrite the optional#get call to safely obtain the value
					//noinspection OptionalGetWithoutIsPresent
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

				Material mat = Items.findMaterial("WOODEN_AXE");

				if (isOld) {
					mat = Items.findMaterial("WOOD_AXE");
				}

				if (e.getItem().getType() == mat) {
					Cuboid.Selection selection = Cuboid.Selection.source(e.getPlayer());
					if (e.getResult() != Event.Result.DENY) {
						e.setResult(Event.Result.DENY);
					}

					// TODO: decide how to handle optional (map setPos2?)
					//noinspection OptionalGetWithoutIsPresent
					selection.setPos2(e.getBlock().get().getLocation());

					new Vent.Call<>(new CuboidSelectEvent(selection) {
					}).run();
				}
			}

		}));

		instance.getEventMap().subscribe(new Vent.Subscription<>(DefaultEvent.PlayerDamagePlayer.class, "region-pvp", instance, Vent.Priority.MEDIUM, (event, subscription) -> {

			Player p = event.getPlayer();
			Player target = event.getVictim();
			Region.Resident r = Region.Resident.get(target);
			if (event.isPhysical()) {

				if (r.getRegion().isPresent()) {
					Region region = r.getRegion().get();

					RegionPVPEvent e = new Vent.Call<>(new RegionPVPEvent(p, target, region)).run();

					if (e.isCancelled()) {
						event.setCancelled(true);
					}
				}
			} else {

				if (r.getRegion().isPresent()) {
					Region region = r.getRegion().get();

					RegionPVPEvent e = new Vent.Call<>(new RegionPVPEvent(p, target, region)).run();

					if (e.isCancelled()) {
						event.setCancelled(true);
					}
				}
			}
		}));
	}

	@Override
	public LabyrinthCollection<Region> getAll() {
		return ImmutableLabyrinthCollection.of(cache);
	}

	@Override
	public Region get(@NotNull Location location) {
		return cache.stream().filter(region -> region.contains(location)).findFirst().orElse(null);
	}

	@Override
	public Region get(@NotNull Player player) {
		return cache.stream().filter(region -> region.contains(player)).findFirst().orElse(null);
	}

	@Override
	public Region get(@NotNull String name) {
		return cache.stream().filter(region -> region.getName().equals(name)).findFirst().orElse(null);
	}

	@Override
	public Region get(@NotNull Location location, boolean passthrough) {
		return cache.stream().filter(region -> region.contains(location) && region.isPassthrough() == passthrough).findFirst().orElse(null);
	}

	@Override
	public Region get(@NotNull Player player, boolean passthrough) {
		return cache.stream().filter(region -> region.contains(player) && region.isPassthrough() == passthrough).findFirst().orElse(null);
	}

	@Override
	public Region get(@NotNull String name, boolean passthrough) {
		return cache.stream().filter(region -> region.getName().equals(name) && region.isPassthrough() == passthrough).findFirst().orElse(null);
	}

	@Override
	public boolean load(@NotNull Region region) {
		if (cache.contains(region)) return false;
		return cache.add(region);
	}

	@Override
	public boolean unload(@NotNull Region region) {
		if (!cache.contains(region)) return false;
		return cache.remove(region);
	}

	@Override
	public Cuboid.FlagManager getFlagManager() {
		return flagManager;
	}
}
