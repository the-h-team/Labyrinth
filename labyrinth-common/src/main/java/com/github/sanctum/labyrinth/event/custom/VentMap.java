package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.api.Service;
import java.util.List;
import java.util.function.Predicate;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

//FIXME
public abstract class VentMap implements Service {

	/**
	 * Unsubscribe from an event by providing the key of the desired subscription if found.
	 *
	 * @param eventType The {@link Vent} to unsubscribe from.
	 * @param key       The key namespace for the subscription.
	 * @param <T>       The inheritance of vent.
	 */
	public abstract <T extends Vent> void unsubscribe(@NotNull Class<T> eventType, @NotNull String key);

	/**
	 * Unsubscribe from an event by providing the key of the desired subscription. All instances of
	 * the found subscription are scheduled for removal.
	 *
	 * @param eventType The {@link Vent} to unsubscribe from.
	 * @param key       The key namespace for the subscription.
	 * @param <T>       The inheritance of vent.
	 */
	public abstract <T extends Vent> void unsubscribeAll(@NotNull Class<T> eventType, @NotNull String key);

	/**
	 * Unsubscribe every found subscription labeled under the specified namespace.
	 *
	 * @param key The key namespace for the subscription.
	 */
	public abstract void unsubscribeAll(@NotNull String key);

	/**
	 * Unsubscribe every found subscription labeled under the specified namespace.
	 *
	 * @param fun The prerequisite to unsubscribing from a vent.
	 */
	public abstract void unsubscribeAll(Predicate<Vent.Subscription<?>> fun);

	/**
	 * Remove a registered subscription listener from cache.
	 *
	 * @param listener The listener to remove.
	 */
	public abstract void unregister(@NotNull Object listener);

	/**
	 * Remove all registered subscription listeners for a plugin from cache.
	 *
	 * @param host The host to query.
	 */
	public abstract void unregisterAll(@NotNull Plugin host);

	/**
	 * Narrow down a list of all subscription listeners provided by a single plugin.
	 *
	 * @param plugin The plugin providing the listeners.
	 * @return A list of all linked listeners.
	 */
	public abstract List<VentListener> list(@NotNull Plugin plugin);

	/**
	 * Narrow down a list of all subscriptions provided by a single plugin.
	 *
	 * @param plugin The plugin providing the subscriptions.
	 * @return A list of all linked subscriptions.
	 */
	public abstract List<Vent.Subscription<?>> narrow(@NotNull Plugin plugin);

	/**
	 * Get a singular listener by its key.
	 *
	 * @param key The key for the listener.
	 * @return The registered listener.
	 */
	public abstract VentListener get(String key);

	/**
	 * Get a singular subscription by its relative key if found.
	 *
	 * @param eventType The {@link Vent} to retrieve the subscription for.
	 * @param key       The namespace for the subscription.
	 * @param <T>       The inheritance of vent.
	 * @return The desired subscription if found otherwise null.
	 */
	public abstract <T extends Vent> Vent.Subscription<T> get(@NotNull Class<T> eventType, @NotNull String key);

	/**
	 * Registers a listener for the given plugin
	 *
	 * @param host     the plugin to set for the listeners
	 * @param listener the listener to be registered
	 */
	public abstract void register(@NotNull Plugin host, @NotNull Object listener);

	/**
	 * Registers multiple listeners at once for the given plugin
	 *
	 * @param host      the plugin to set for the listeners
	 * @param listeners the listeners to be registered
	 */
	public abstract void registerAll(@NotNull Plugin host, @NotNull Object... listeners);

	/**
	 * Adds a subscription to this mapping.
	 *
	 * @param subscription the subscription to be added
	 * @param <T>          the vent type of the subscription
	 */
	public abstract <T extends Vent> void subscribe(Vent.Subscription<T> subscription);

	/**
	 * Adds chained subscriptions to this map
	 *
	 * @param link the subscription container
	 */
	public abstract void chain(Vent.Link link);

	public abstract List<Vent.Subscription<?>> getSubscriptions();

	public abstract List<VentListener> getListeners();
}
