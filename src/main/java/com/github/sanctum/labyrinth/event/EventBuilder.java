package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Registry;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EventBuilder {

	private final Plugin plugin;

	public EventBuilder(Plugin instance) {
		this.plugin = instance;
	}

	/**
	 * Look for any compatible object types representative of Listener within a
	 * desired package location and automatically register each of them individually if possible.
	 *
	 * @param packageName The location to query.
	 */
	@Deprecated
	public void compileFields(String packageName) {
		try {
			new Registry<>(Listener.class)
					.source(this.plugin)
					.pick(packageName)
					.operate(EventBuilder::register);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Look for any compatible object types representative of Listener within a
	 * desired package location and automatically register each of them individually if possible.
	 *
	 * @param packageName The location to query.
	 */
	@Deprecated
	public static void compileFields(Plugin plugin, String packageName) {
		try {
			new Registry<>(Listener.class)
					.source(plugin)
					.pick(packageName)
					.operate(EventBuilder::register);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Register a specific listener.
	 *
	 * @param listener The listener to be registered.
	 */
	public static void register(Listener listener) {
		Plugin plugin = JavaPlugin.getProvidingPlugin(listener.getClass());
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

}
