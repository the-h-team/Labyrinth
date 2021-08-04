package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import org.bukkit.plugin.Plugin;

public abstract class VentMap {

	final LinkedList<Vent.Subscription<?>> subscriptions = new LinkedList<>();

	/**
	 * Unsubscribe from an event by providing the key of the desired subscription if found.
	 *
	 * @param eventType The {@link Vent} to unsubscribe from.
	 * @param key       The key namespace for the subscription.
	 * @param <T>       The inheritance of vent.
	 */
	public abstract <T extends Vent> void unsubscribe(Class<T> eventType, String key);

	/**
	 * Unsubscribe from an event by providing the key of the desired subscription. All instances of
	 * the found subscription are scheduled for removal.
	 *
	 * @param eventType The {@link Vent} to unsubscribe from.
	 * @param key       The key namespace for the subscription.
	 * @param <T>       The inheritance of vent.
	 */
	public abstract <T extends Vent> void unsubscribeAll(Class<T> eventType, String key);

	/**
	 * Unsubscribe every found subscription labeled under the specified namespace.
	 *
	 * @param key       The key namespace for the subscription.
	 */
	public abstract void unsubscribeAll(String key);

	/**
	 * Unsubscribe every found subscription labeled under the specified namespace.
	 *
	 * @param fun       The prerequisite to unsubscribing from a vent.
	 */
	public abstract void unsubscribeAll(Predicate<Vent.Subscription<?>> fun);

	/**
	 * Narrow down a list of all subscriptions provided by a single plugin.
	 *
	 * @param plugin The plugin providing the subscriptions.
	 * @return A list of all linked subscriptions.
	 */
	public abstract List<Vent.Subscription<?>> narrow(Plugin plugin);

	/**
	 * Get a singular subscription by its relative key if found.
	 *
	 * @param eventType The {@link Vent} to retrieve the subscription for.
	 * @param key       The namespace for the subscription.
	 * @param <T>       The inheritance of vent.
	 * @return The desired subscription if found otherwise null.
	 */
	public abstract <T extends Vent> Vent.Subscription<T> get(Class<T> eventType, String key);

	public static VentMap getInstance() {
		return LabyrinthProvider.getInstance().getEventMap();
	}

}
