package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.data.container.Region;
import com.github.sanctum.panther.container.PantherCollection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Used to provide registration to many region related services.
 */
public abstract class RegionServicesManager {

	public abstract PantherCollection<Region> getAll();

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
	public abstract FlagManager getFlagManager();

	public abstract ItemStack getWand();

	public abstract void setWand(@NotNull ItemStack itemStack);

	/**
	 * Get the main region service manager instance.
	 *
	 * @return the region services manager
	 */
	public static RegionServicesManager getInstance() {
		return Bukkit.getServicesManager().load(RegionServicesManager.class);
	}

}
