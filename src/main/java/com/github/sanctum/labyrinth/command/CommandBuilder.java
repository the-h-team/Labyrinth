package com.github.sanctum.labyrinth.command;

import com.google.common.collect.Sets;
import com.github.sanctum.labyrinth.Labyrinth;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandBuilder {

	Plugin plugin;

	public CommandBuilder(Plugin instance) {
		this.plugin = instance;
	}

	/**
	 * Look for any compatible object types representative of Command within a
	 * desired package location and automatically register each of them individually if possible.
	 * @param packageName The location to query.
	 */
	public void compileFields(String packageName) {
		Set<Class<?>> classes = Sets.newHashSet();
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(URLDecoder.decode(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (JarEntry jarEntry : Collections.list(jarFile.entries())) {
			String className = jarEntry.getName().replace("/", ".");
			if (className.startsWith(packageName) && className.endsWith(".class")) {
				Class<?> clazz;
				try {
					clazz = Class.forName(className.substring(0, className.length() - 6));
				} catch (ClassNotFoundException e) {
					Labyrinth.getInstance().getLogger().severe("- Unable to find class" + className + "! Double check package location. See the error below for more information.");
					e.printStackTrace();
					break;
				}
				if (Command.class.isAssignableFrom(clazz)) {
					classes.add(clazz);
				}
			}
		}
		for (Class<?> aClass : classes) {
			try {
				Command command = ((Command) aClass.getDeclaredConstructor().newInstance());
				final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
				commandMapField.setAccessible(true);

				final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
				commandMap.register(command.getLabel(), plugin.getName(), command);
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException e) {
				Labyrinth.getInstance().getLogger().severe("- Unable to cast Command to the class " + aClass.getName() + ". This likely means you are not extending Command for your command class.");
				e.printStackTrace();
				break;
			}
		}
	}

	/**
	 * Look for any compatible object types representative of Command within a
	 * desired package location and automatically register each of them individually if possible.
	 * @param packageName The location to query.
	 */
	public static void compileFields(Plugin plugin, String packageName) {
		Set<Class<?>> classes = Sets.newHashSet();
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(URLDecoder.decode(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (JarEntry jarEntry : Collections.list(jarFile.entries())) {
			String className = jarEntry.getName().replace("/", ".");
			if (className.startsWith(packageName) && className.endsWith(".class")) {
				Class<?> clazz;
				try {
					clazz = Class.forName(className.substring(0, className.length() - 6));
				} catch (ClassNotFoundException e) {
					Labyrinth.getInstance().getLogger().severe("- Unable to find class" + className + "! Double check package location. See the error below for more information.");
					e.printStackTrace();
					break;
				}
				if (Command.class.isAssignableFrom(clazz)) {
					classes.add(clazz);
				}
			}
		}
		for (Class<?> aClass : classes) {
			try {
				Command command = ((Command) aClass.getDeclaredConstructor().newInstance());
				final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
				commandMapField.setAccessible(true);

				final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
				commandMap.register(command.getLabel(), plugin.getName(), command);
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException e) {
				Labyrinth.getInstance().getLogger().severe("- Unable to cast Command to the class " + aClass.getName() + ". This likely means you are not extending Command for your command class.");
				e.printStackTrace();
				break;
			}
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
