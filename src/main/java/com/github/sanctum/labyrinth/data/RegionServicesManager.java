package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public final class RegionServicesManager {

	private final LinkedList<Cuboid.Flag> FLAGS = new LinkedList<>();

	private final LinkedList<RegionService> SERVICES = new LinkedList<>();

	public List<Cuboid.Flag> getFlags() {
		return FLAGS;
	}

	public static RegionServicesManager getInstance() {
		return Bukkit.getServicesManager().load(RegionServicesManager.class);
	}

	public boolean isRegistered(Cuboid.Flag flag) {
		return this.FLAGS.stream().anyMatch(f -> f.getId().equals(flag.getId()));
	}

	public boolean unregister(Cuboid.Flag flag) {
		for (Region r : Region.cache().list()) {
			r.getFlags().forEach(f -> {
				if (f.getId().equals(flag.getId())) {
					Schedule.sync(() -> r.removeFlag(f)).run();
				}
			});
		}
		return FLAGS.removeIf(f -> f.getId().equals(flag.getId()));
	}

	public boolean register(Cuboid.Flag flag) {
		if (FLAGS.stream().noneMatch(f -> f.getId().equals(flag.getId()))) {
			FLAGS.add(flag);
			return true;
		}
		return false;
	}

	public boolean load(Cuboid.Flag flag) {
		Bukkit.getPluginManager().registerEvents(flag, Labyrinth.getInstance());
		return SERVICES.add(flag) && FLAGS.add(flag);
	}

	public boolean load(RegionService service) {
		Bukkit.getPluginManager().registerEvents(service, Labyrinth.getInstance());
		return SERVICES.add(service);
	}

	public boolean unload(RegionService service) {
		HandlerList.unregisterAll(service);
		return SERVICES.remove(service);
	}

}
