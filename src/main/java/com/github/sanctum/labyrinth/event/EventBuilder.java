package com.github.sanctum.labyrinth.event;

import com.google.common.collect.Sets;
import com.github.sanctum.labyrinth.Labyrinth;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class EventBuilder {

	Plugin plugin;

	public EventBuilder(Plugin instance) {
		this.plugin = instance;
	}

	/**
	 * Look for any compatible object types representative of Bukkit interface Listener within a
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
					break;
				}
				if (Listener.class.isAssignableFrom(clazz)) {
					classes.add(clazz);
				}
			}
		}
		for (Class<?> aClass : classes) {
			try {
				Bukkit.getPluginManager().registerEvents((Listener) aClass.getDeclaredConstructor().newInstance(), plugin);
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				Labyrinth.getInstance().getLogger().severe("- Unable to cast Listener to the class " + aClass.getName() + ". This likely means you are not implementing the Listener interface for your event class.");
				e.printStackTrace();
				break;
			}
		}
	}

}
