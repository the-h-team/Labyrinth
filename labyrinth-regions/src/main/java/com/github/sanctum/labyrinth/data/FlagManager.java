package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.container.Cuboid;
import com.github.sanctum.labyrinth.data.container.Region;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.event.Vent;
import com.github.sanctum.panther.event.VentMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.bukkit.plugin.Plugin;

public class FlagManager {

	private final Plugin plugin = LabyrinthProvider.getInstance().getPluginInstance();
	private final Set<Cuboid.Flag> CACHE = new HashSet<>();
	private final RegionServicesManager regionServices;


	public FlagManager(RegionServicesManager manager) {
		this.regionServices = manager;
		Cuboid.Flag BREAK = DefaultFlag.Builder
				.initialize()
				.label("break")
				.finish();

		Cuboid.Flag BUILD = DefaultFlag.Builder
				.initialize()
				.label("build")
				.finish();

		Cuboid.Flag PVP = DefaultFlag.Builder
				.initialize()
				.label("pvp")
				.finish();

		register(PVP);
		register(BREAK);
		register(BUILD);

	}

	public Optional<Cuboid.Flag> getFlag(String id) {
		return CACHE.stream().filter(f -> f.getId().equals(id)).findFirst();
	}

	public Set<Cuboid.Flag> getFlags() {
		return Collections.unmodifiableSet(CACHE);
	}

	public boolean isRegistered(Cuboid.Flag flag) {
		return CACHE.stream().anyMatch(f -> f.getId().equals(flag.getId()));
	}

	public boolean unregister(Cuboid.Flag flag) {
		for (Region r : regionServices.getAll()) {
			r.getFlags().forEach(f -> {
				if (f.getId().equals(flag.getId())) {
					TaskScheduler.of(() -> r.removeFlag(f)).schedule();
				}
			});
		}
		return CACHE.removeIf(f -> f.getId().equals(flag.getId()));
	}

	public boolean register(Cuboid.Flag flag) {
		if (!getFlag(flag.getId()).isPresent()) {
			regionServices.getAll().forEach(region -> region.addFlag(flag));
			return CACHE.add(flag);
		}
		return false;
	}

	public boolean registerControlling(Cuboid.Flag flag) {
		if (!getFlag(flag.getId()).isPresent()) {
			VentMap.getInstance().subscribe((Vent.Host) plugin, flag);
			regionServices.getAll().forEach(region -> region.addFlag(flag));
			return CACHE.add(flag);
		}
		return false;
	}

}
