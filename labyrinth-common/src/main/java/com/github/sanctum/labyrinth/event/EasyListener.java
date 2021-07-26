package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

public class EasyListener {

	private Listener listener;

	private Class<? extends Listener> impl;

	public EasyListener(Listener listener) {
		this.listener = listener;
	}

	public EasyListener(Class<? extends Listener> impl) {
		this.impl = impl;
	}

	public void call(Plugin plugin) {
		try {
			this.listener = impl.getDeclaredConstructor().newInstance();
			register(plugin);
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			LabyrinthProvider.getInstance().getLogger().warning("- Listener " + (impl != null ? impl.getSimpleName() : "Unknown") + " wasn't able to instantiate. Ensure the constructor contains no parameters.");
		}
	}

	/**
	 * Register the listener.
	 */
	protected void register(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	/**
	 * Simple listener registration.
	 *
	 * @param plugin The plugin that's providing the listener.
	 * @param listener The listener to register.
	 */
	public static void call(Plugin plugin, Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

}
