package com.github.sanctum.labyrinth.data;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.ConcurrentHashMap;

public class FileList {
	// Outer key = plugin name. Inner key = "d;n" where d and n represent the respective fields
	static final Map<String, Map<String, FileManager>> CACHE = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, FileList> REGISTRY = new ConcurrentHashMap<>();

	private final Plugin plugin;

	private FileList(Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Using a {@link Plugin plugin} as the key, look for specific files.
	 * <p>
	 * <strong>Design:</strong> <em>Using your plugin's main class instance,
	 * create custom data files with ease--sourced direct from your plugin's
	 * data folder.</em>
	 *
	 * @param plugin the plugin source to browse
	 * @return a potential listing of configuration
	 */
	public static FileList search(@NotNull final Plugin plugin) {
		return REGISTRY.computeIfAbsent(plugin.getName(), name -> new FileList(plugin));
	}

	/**
	 * Get the list of all cached file managers for a specified plugin.
	 *
	 * @param plugin the plugin to retrieve file management for
	 * @return a list of file managers if the plugin is cached;
	 * otherwise, an empty List
	 */
	public static List<FileManager> getFiles(Plugin plugin) {
		return Optional.ofNullable(CACHE.get(plugin.getName()))
				.map(Map::values)
				.map(ImmutableList::copyOf)
				.orElse(ImmutableList.of());
	}

	/**
	 * Get the plugin attached to the search.
	 *
	 * @return the plugin attached to this file search
	 */
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Copy a file of any type from this listings plugin.
	 *
	 * @param fileName the name of the file following the file type
	 * @param fileManager the FileManager instance to copy to
	 */
	public void copy(String fileName, FileManager fileManager) {
		//noinspection ConstantConditions
		FileManager.copy(this.plugin.getResource(fileName), fileManager);
	}

	/**
	 * Copy a yml file from this listings plugin to a manager of specification.
	 *
	 * @param fileName the file name only
	 * @param fileManager the FileManager instance to copy to
	 */
	public void copyYML(String fileName, FileManager fileManager) {
		//noinspection ConstantConditions
		FileManager.copy(this.plugin.getResource(fileName + ".yml"), fileManager);
	}

	/**
	 * Copy a yml file from this listings plugin to a file of specification.
	 *
	 * @param fileName the file name only
	 * @param file the file instance to copy to
	 */
	public void copyYML(String fileName, File file) {
		//noinspection ConstantConditions
		FileManager.copy(this.plugin.getResource(fileName + ".yml"), file);
	}

	/**
	 * Retrieve a Config instance via its name and description.
	 * <p>
	 * This method resolves config objects for any {@link org.bukkit.plugin.java.JavaPlugin}
	 * main class passed through the initial search query.
	 *
	 * @param name name of config file
	 * @param desc description of config file (designate subdirectory)
	 * @return existing instance or create new Config
	 * @throws IllegalArgumentException if name is empty
	 */
	public @NotNull FileManager find(@NotNull final String name, @Nullable final String desc) throws IllegalArgumentException {
		// move up to fail fast
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Name cannot be empty!");
		}
		// See CACHE declaration above for new key strategy
		return Optional.ofNullable(CACHE.get(plugin.getName()))
				.map(m -> m.get(desc + ';' + name))
				.orElseGet(() -> cacheFileManager(new FileManager(plugin, name, desc)));
	}

	private static FileManager cacheFileManager(FileManager fileManager) {
		CACHE.computeIfAbsent(fileManager.plugin.getName(), s -> new ConcurrentHashMap<>()).put(fileManager.d + ';' + fileManager.n, fileManager);
		return fileManager;
	}

	/**
	 * Retrieve a Config instance via its name and description.
	 * <p>
	 * This method resolves config objects for any {@link org.bukkit.plugin.java.JavaPlugin}
	 * main class passed through the initial search query.
	 *
	 * @param name name of config file
	 * @return existing instance or create new Config
	 * @throws IllegalArgumentException if name is empty
	 */
	public @NotNull FileManager find(@NotNull final String name) throws IllegalArgumentException {
		return find(name, null);
	}

}
