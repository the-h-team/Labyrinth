package com.github.sanctum.labyrinth.data;

import com.google.common.collect.ImmutableList;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Encapsulates a plugin for quick and easy file locating/management.
 */
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

	public List<FileManager> getFiles() {
		return Optional.ofNullable(CACHE.get(plugin.getName()))
				.map(Map::values)
				.map(ImmutableList::copyOf)
				.orElse(ImmutableList.of());
	}

	/**
	 * Inject a custom implementation of configuration into cache for global use.
	 *
	 * @param configurable The implementation of configurable to inject.
	 */
	public void inject(@NotNull Configurable configurable) {
		cacheFileManager(new FileManager(plugin, configurable));
	}

	public void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File is a directory!", e);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to write to file! See log:", e);
		}
	}

	/**
	 * Copy a file of any type from this listings plugin.
	 *
	 * @param fileName the name of the file following the file type
	 * @param fileManager the FileManager instance to copy to
	 */
	public void copy(String fileName, FileManager fileManager) {
		InputStream stream = this.plugin.getResource(fileName);
		if (stream == null) throw new RuntimeException("Non existent resources cannot be copied to file managers!");
		copy(stream, fileManager.getRoot().getParent());
	}

	/**
	 * Copy a yml file from this listings plugin to a manager of specification.
	 *
	 * @param ymlName the file name only
	 * @param fileManager the FileManager instance to copy to
	 */
	public void copyYML(String ymlName, FileManager fileManager) {
		copy(ymlName + ".yml", fileManager);
	}

	/**
	 * Copy a yml file from this listings plugin to a file of specification.
	 *
	 * @param ymlName the file name only
	 * @param file the file instance to copy to
	 */
	public void copyYML(String ymlName, File file) {
		InputStream stream = this.plugin.getResource(ymlName + ".yml");
		if (stream == null) throw new RuntimeException("Non existent resources cannot be copied to file locations!");
		copy(stream, file);
	}

	/**
	 * Retrieve a Config instance via its name and description.
	 * <p>
	 * This method resolves config objects for any {@link org.bukkit.plugin.java.JavaPlugin}
	 * main class passed through the initial search query.
	 *
	 * @param name name of config file
	 * @param desc description of config file (designate subdirectory
	 * @param type Whether or not to use JSON or YML
	 * @return existing instance or create new Config
	 * @throws IllegalArgumentException if name is empty
	 */
	public @NotNull FileManager find(@NotNull final String name, @Nullable final String desc, final FileType type) throws IllegalArgumentException {
		// move up to fail fast
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Name cannot be empty!");
		}
		// See CACHE declaration above for new key strategy
		return Optional.ofNullable(CACHE.get(plugin.getName()))
				.map(m -> m.get(desc + ';' + name))
				.filter(m -> m.getRoot().getType() == type)
				.orElseGet(() -> cacheFileManager(new FileManager(plugin, null, name, desc, type)));
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
		return find(name, desc, FileType.YAML);
	}

	private static FileManager cacheFileManager(FileManager fileManager) {
		CACHE.computeIfAbsent(fileManager.plugin.getName(), s -> new ConcurrentHashMap<>()).put(fileManager.configuration.getDirectory() + ';' + fileManager.configuration.getName(), fileManager);
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
		return find(name, (String) null);
	}

	/**
	 * Retrieve a Config instance via its name and description.
	 * <p>
	 * This method resolves config objects for any {@link org.bukkit.plugin.java.JavaPlugin}
	 * main class passed through the initial search query.
	 *
	 * @param name name of config file
	 * @param data Whether or not to use JSON or YML, true = JSON
	 * @return existing instance or create new Config
	 * @throws IllegalArgumentException if name is empty
	 */
	public @NotNull FileManager find(@NotNull final String name, final FileType data) throws IllegalArgumentException {
		return find(name, null, data);
	}

}
