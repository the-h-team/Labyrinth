package com.github.sanctum.labyrinth.command;

import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandRegistration {

	private final Class<? extends Command> command;

	public CommandRegistration(Class<? extends Command> command) {
		this.command = command;
	}

	/**
	 * Register the command into the command map.
	 */
	public void register() {
		try {
			Command cmd = command.getDeclaredConstructor().newInstance();
			Plugin plugin = JavaPlugin.getProvidingPlugin(cmd.getClass());
			final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);

			final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
			commandMap.register(cmd.getLabel(), plugin.getName(), cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void use(Command cmd) {
		try {
			Plugin plugin = JavaPlugin.getProvidingPlugin(cmd.getClass());
			final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);

			final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
			commandMap.register(cmd.getLabel(), plugin.getName(), cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
