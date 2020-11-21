package com.youtube.hempfest.hempcore.command;

import com.google.common.collect.Sets;
import com.youtube.hempfest.hempcore.HempCore;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;

public class CommandBuilder {

	Plugin plugin;

	public CommandBuilder(Plugin instance) {
		this.plugin = instance;
	}

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
					HempCore.getInstance().getLogger().severe("- Unable to find class" + className + "! Make sure you spelled it correctly when using the registerAllCommandsAutomatically method. See the error below for an exact line.");
					break;
				}
				if (BukkitCommand.class.isAssignableFrom(clazz)) {
					classes.add(clazz);
				}
			}
		}
		for (Class<?> aClass : classes) {
			try {
				BukkitCommand command = ((BukkitCommand) aClass.getDeclaredConstructor().newInstance());
				final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
				commandMapField.setAccessible(true);

				final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
				commandMap.register(command.getLabel(), command);
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException e) {
				HempCore.getInstance().getLogger().severe("- Unable to cast BukkitCommand to the class " + aClass.getName() + ". This likely means you are not extending BukkitCommand for your command class.");
				e.printStackTrace();
				break;
			}
		}
	}


}
