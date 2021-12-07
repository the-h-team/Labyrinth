package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.library.Cuboid;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Used to provide registration to many region related services.
 */
public abstract class RegionServicesManager {

	/**
	 * Get the main region service manager instance.
	 *
	 * @return the region services manager
	 */
	public static RegionServicesManager getInstance() {
		return Bukkit.getServicesManager().load(RegionServicesManager.class);
	}

	public abstract List<Region> getAll();

	public abstract Region get(@NotNull Location location);

	public abstract Region get(@NotNull Player player);

	public abstract Region get(@NotNull String name);

	public abstract Region get(@NotNull Location location, boolean passthrough);

	public abstract Region get(@NotNull Player player, boolean passthrough);

	public abstract Region get(@NotNull String name, boolean passthrough);

	public abstract boolean load(@NotNull Region region);

	public abstract boolean unload(@NotNull Region region);

	/**
	 * Manage all cuboid region flags.
	 *
	 * @return this manager's flag manager
	 */
	public abstract Cuboid.FlagManager getFlagManager();

}
