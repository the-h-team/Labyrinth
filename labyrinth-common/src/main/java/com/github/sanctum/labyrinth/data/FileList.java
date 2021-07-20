package com.github.sanctum.labyrinth.data;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class FileList {

	protected static final Map<Plugin, List<FileManager>> CACHE = new HashMap<>();
	private static final LinkedList<FileList> REGISTRY = new LinkedList<>();
	private final Plugin plugin;

	private FileList(Plugin plugin) {
		this.plugin = plugin;
		REGISTRY.add(this);
	}

	/**
	 * Using a {@link Plugin plugin} as the key, look for specific files.
	 * <p>
	 * <strong>Design:</strong> <em>Using your plugin's main class instance,
	 * create custom data files with ease--sourced direct from your plugin's
	 * data folder.</em>
	 *
	 * @param plugin The plugin source to browse
	 * @return A potential listing of configuration
	 */
	public static FileList search(@NotNull final Plugin plugin) {
		FileList list = null;
		for (FileList listing : REGISTRY) {
			if (listing.plugin.equals(plugin)) {
				list = listing;
				break;
			}
		}
		return list != null ? list : new FileList(plugin);
	}

	/**
	 * Get the list of all cached file managers for a specified plugin.
	 *
	 * @param plugin the plugin to retrieve file management for
	 * @return a list of file managers if the plugin is cached;
	 * otherwise, an empty List
	 */
	public static List<FileManager> getFiles(Plugin plugin) {
		return CACHE.entrySet().stream().filter(e -> e.getKey().getName().equals(plugin.getName())).map(Map.Entry::getValue).findFirst().orElse(new ArrayList<>());
	}

	/**
	 * Get the plugin attached to the search.
	 *
	 * @return The plugin attached to this file search
	 */
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Copy a file of any type from this listings plugin.
	 *
	 * @param fileName     The name of the file following the file type
	 * @param fileManager The FileManager instance to copy to
	 */
	public void copy(String fileName, FileManager fileManager) {
		//noinspection ConstantConditions
		FileManager.copy(this.plugin.getResource(fileName), fileManager);
	}

	/**
	 * Copy a yml file from this listings plugin to a manager of specification.
	 *
	 * @param fileName     The file name only
	 * @param fileManager The FileManager instance to copy to
	 */
	public void copyYML(String fileName, FileManager fileManager) {
		//noinspection ConstantConditions
		FileManager.copy(this.plugin.getResource(fileName + ".yml"), fileManager);
	}

	/**
	 * Copy a yml file from this listings plugin to a file of specification.
	 *
	 * @param fileName The file name only
	 * @param file     The file instance to copy to
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
	 * @param name Name of config file
	 * @param desc Description of config file (designate subdirectory)
	 * @return existing instance or create new Config
	 * @throws IllegalArgumentException if name is empty
	 */
	public @NotNull FileManager find(@NotNull final String name, final String desc) throws IllegalArgumentException {
		// move up to fail fast
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Name cannot be empty!");
		}
		FileManager result = null;
		// switch to stream api
		final int hashCode = Objects.hash(name, desc);
		if (CACHE.containsKey(this.plugin)) {
			for (Map.Entry<Plugin, List<FileManager>> entry : CACHE.entrySet()) {
				if (entry.getKey().equals(this.plugin)) {
					if (entry.getValue().stream().anyMatch(fm -> fm.hashCode() == hashCode)) {
						result = entry.getValue().stream().filter(fm -> fm.hashCode() == hashCode)
								.findFirst()
								.orElseGet(() -> new FileManager(this.plugin, name, desc));
					}
					break;
				}
			}
		} else {
			result = new FileManager(this.plugin, name, desc);
		}
		return result != null ? result : new FileManager(this.plugin, name, desc);
	}


}
