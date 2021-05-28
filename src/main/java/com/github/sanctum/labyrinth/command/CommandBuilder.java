package com.github.sanctum.labyrinth.command;

import com.github.sanctum.labyrinth.data.Registry;
import java.io.IOException;
import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandBuilder {

	private final Plugin plugin;

	public CommandBuilder(Plugin instance) {
		this.plugin = instance;
	}

	/**
	 * Look for any compatible object types representative of Command within a
	 * desired package location and automatically register each of them individually if possible.
	 *
	 * @param packageName The location to query.
	 */
	@Deprecated
	public void compileFields(String packageName) {
		try {
			new Registry<>(Command.class)
					.source(this.plugin)
					.pick(packageName)
					.operate(cmd -> {
						Field commandMapField = null;
						try {
							commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						}
						if (commandMapField != null) {
							commandMapField.setAccessible(true);
							CommandMap commandMap = null;
							try {
								commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
							if (commandMap != null) {
								commandMap.register(cmd.getLabel(), plugin.getName(), cmd);
							}
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Look for any compatible object types representative of Command within a
	 * desired package location and automatically register each of them individually if possible.
	 *
	 * @param packageName The location to query.
	 */
	@Deprecated
	public static void compileFields(Plugin plugin, String packageName) {
		try {
			new Registry<>(Command.class)
					.source(plugin)
					.pick(packageName)
					.operate(cmd -> {
						Field commandMapField = null;
						try {
							commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						}
						if (commandMapField != null) {
							commandMapField.setAccessible(true);
							CommandMap commandMap = null;
							try {
								commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
							if (commandMap != null) {
								commandMap.register(cmd.getLabel(), plugin.getName(), cmd);
							}
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Register a specific command into the command map.
	 *
	 * @param command The command to be registered.
	 */
	public static void register(Command command) {
		Plugin plugin = JavaPlugin.getProvidingPlugin(command.getClass());
		try {
			final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);

			final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
			commandMap.register(command.getLabel(), plugin.getName(), command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
