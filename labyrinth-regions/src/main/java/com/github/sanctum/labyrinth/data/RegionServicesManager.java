package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.library.Cuboid;
import org.bukkit.Bukkit;

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

	/**
	 * Check if a flag is currently registered.
	 *
	 * @param flag the flag to check
	 * @return false if not registered or null
	 */
	public abstract boolean isRegistered(Cuboid.Flag flag);

	/**
	 * Unregister a flag from cache.
	 *
	 * @param flag the flag to unregister
	 * @return false if null or not registered
	 */
	public abstract boolean unregister(Cuboid.Flag flag);

	/**
	 * Register a flag into cache.
	 *
	 * @param flag the flag to register
	 * @return false if already registered or null
	 */
	public abstract boolean register(Cuboid.Flag flag);

	/**
	 * Register a flag into cache and its accompanied listener.
	 *
	 * @param flag the flag to register
	 * @return false if already registered or null
	 */
	public abstract boolean load(Cuboid.Flag flag);

	/**
	 * Load a subscription to a region event or other into cache.
	 *
	 * @param subscription the subscription to invoke
	 */
	public abstract void load(Vent.Subscription<?> subscription);

	/**
	 * Unload a subscription by its relative namespace.
	 *
	 * @param type the event type to unsubscribe from
	 * @param key  the namespace of the subscription
	 * @param <T>  the inheritance of vent
	 */
	public abstract <T extends Vent> void unload(Class<T> type, String key);

	/**
	 * Unload all subscriptions by the same relative namespace.
	 *
	 * @param type the event type to unsubscribe from
	 * @param key  the namespace of the subscription
	 * @param <T>  the inheritance of vent
	 */
	public abstract <T extends Vent> void unloadAll(Class<T> type, String key);

	/**
	 * Manage all cuboid region flags.
	 *
	 * @return this manager's flag manager
	 */
	public abstract Cuboid.FlagManager getFlagManager();

}
