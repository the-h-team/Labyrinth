package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.api.Service;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//FIXME
public abstract class VentMap implements Service {

	/**
	 * Unsubscribe from an event by providing the key of the desired subscription if found.
	 *
	 * @param eventType The {@link Vent} to unsubscribe from.
	 * @param key       The key namespace for the subscription.
	 * @param <T>       The inheritance of vent.
	 * @deprecated As there could be multiple entries for one key, you cannot be sure to remove the right listener!
	 * And in the only case where it would always remove the right key,
	 * the method is equivalent to {{@link #unsubscribeAll(Class, String)}}
	 */
	@Deprecated
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
	 * The most preferred way to remove subscriptions.
	 *
	 * @param subscription the subscription instance to remove;
	 */
	public abstract void unsubscribe(Vent.Subscription<?> subscription);

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
	 * @deprecated Takes long O(n) to find the listener. Use {{@link #unregister(Plugin, String, Object)}} for O(1)!
	 */
	public abstract void unregister(@NotNull Object listener);

	/**
	 * Removes a registered subscription listener by specification.
	 *
	 * @param host the plugin providing the listener
	 * @param key  the key associated with the listener or null if none was specified
	 */
	public abstract void unregister(Plugin host, @Nullable String key, Object listener);

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
	 * @deprecated A key can hold multiple listeners. Will be disabled in the future.
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

	public abstract <T extends Vent> Stream<Vent.Subscription<T>> getSubscriptions(Class<T> tClass,
																				   Vent.Priority priority);

	public abstract List<VentListener> getListeners();
}
