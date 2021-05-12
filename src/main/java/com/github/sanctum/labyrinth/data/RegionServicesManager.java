package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public final class RegionServicesManager {

	private final LinkedList<RegionService> SERVICES = new LinkedList<>();

	private final Cuboid.FlagManager flagManager;

	{
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
}
