package com.github.sanctum.labyrinth.data;

import org.bukkit.plugin.Plugin;

/**
 * Delegates file type information in regards to either Json or Yaml formats.
 *
 * @author Hempfest
 * @version 1.0
 */
public enum FileType {

	/**
	 * A file responsible for standard config operations.
	 */
	YAML(YamlConfiguration.class),
	/**
	 * A file responsible for things like user data/ data of any type.
	 */
	JSON(JsonConfiguration.class),
	/**
	 * A custom implementation of configurable.
	 */
	CUSTOM(Configurable.class),
	/**
	 * An un-specified implementation of configurable.
	 */
	UNKNOWN(Configurable.class);

	private final Class<? extends Configurable> c;

	FileType(Class<? extends Configurable> cl) {
		this.c = cl;
	}

	public FileManager getNewFile(Plugin plugin, String name) {
		return FileList.search(plugin).find(name, this);
	}

	public FileManager getNewFile(Plugin plugin, String name, String directory) {
		return FileList.search(plugin).find(name, directory, this);
	}

	/**
	 * Attempts to get the super class type.
	 *
	 * <p>If the type represents a custom implementation then the base Configurable class is returned.</p>
	 *
	 * @return The possible super class for this type.
	 */
	public Class<? extends Configurable> getImplementation() {
		return c;
	}
}
