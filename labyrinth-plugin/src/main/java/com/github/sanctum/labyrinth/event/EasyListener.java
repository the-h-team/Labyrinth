package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.Labyrinth;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

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
			Labyrinth.getInstance().getLogger().warning("- Listener " + (impl != null ? impl.getSimpleName() : "Unknown") + " wasn't able to instantiate. Ensure the constructor contains no parameters.");
		}
	}

	/**
	 * Register the listener.
	 */
	protected void register(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

}
