package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.annotation.Experimental;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Encapsulates file operations for data saving/retrieval
 */
public class FileManager {

	protected final Configurable configuration;
	protected final Plugin plugin;

	protected FileManager(@NotNull Plugin plugin, @NotNull Configurable configuration) {
		this.plugin = plugin;
		this.configuration = configuration;
	}

	protected FileManager(@NotNull Plugin plugin, @NotNull final String n, @Nullable final String d, FileType type) {
		this(plugin, null, n, d, type);
	}

	protected FileManager(@NotNull Plugin plugin, @Nullable final String type, @NotNull final String n, @Nullable final String d, FileType data) {
		if (data == FileType.JSON) {
			JsonConfiguration c = new JsonConfiguration(plugin, type, n, d);
			this.plugin = c.plugin;
			this.configuration = c;
		} else {
			YamlConfiguration c = new YamlConfiguration(plugin, n, d);
			this.plugin = c.plugin;
			this.configuration = c;
		}
	}

	/**
	 * Copy an InputStream directly to a given File.
	 * <p>
	 * Useful for placing resources retrieved from a JavaPlugin
	 * implementation at custom locations.
	 *
	 * @param in   an InputStream, likely a plugin resource
	 * @param file the desire file
	 * @throws IllegalArgumentException if the file describes a directory
	 * @throws IllegalStateException    if write is unsuccessful
	 */
	public static void copy(InputStream in, File file) {
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
	 * Copy an InputStream directly to a given File.
	 * <p>
	 * Useful for placing resources retrieved from a JavaPlugin
	 * implementation at custom locations.
	 *
	 * @param in      an InputStream, likely a plugin resource
	 * @param manager the manager to locate to
	 * @throws IllegalArgumentException if the file describes a directory
	 * @throws IllegalStateException    if write is unsuccessful
	 */
	public static void copy(InputStream in, FileManager manager) {
		try {
			OutputStream out = new FileOutputStream(manager.getChild().getParent());
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

	public Configurable getChild() {
		return configuration;
	}

	/**
	 * Performs operations on this FileManager's config instance,
	 * returning an object of any desired type. Accepts a lambda,
	 * allowing for clean and compile-time-type-safe data retrieval and
	 * mapping.
	 *
	 * @param fun an operation returning an object of arbitrary type
	 *            {@link R} from the configuration
	 * @param <R> type of the returned object (inferred)
	 * @return the value produced by the provided function
	 */
	public <R> R read(Function<Configurable, R> fun) {
		return fun.apply(configuration);
	}

	/**
	 * Set & save multiple keyed value spaces within this file.
	 * After all inquires have been transferred the inquiry object is cleared and discarded due to
	 * being of no further importance.
	 *
	 * Takes all 2d declarations and forms them into multi-layered nodes.
	 *
	 * @param map The file writer to use when setting values.
	 */
	@Experimental("Is this something we should stick with? Seems creative to me :) -Hemp")
	public void write(DataMap map) {
		write(map, true);
	}

	/**
	 * @see FileManager#write(DataMap)
	 */
	@Experimental("Is this something we should stick with? Seems creative to me :) -Hemp")
	public void write(DataMap map, boolean replace) {
		for (Map.Entry<String, Object> entry : map.get().entrySet()) {
			if (replace) {
				if (entry.getValue().equals("NULL")) {
					configuration.set(entry.getKey(), null);
				} else {
					configuration.set(entry.getKey(), entry.getValue());
				}
			} else {
				if (!entry.getValue().equals("NULL")) {
					if (!configuration.isNode(entry.getKey())) {
						configuration.set(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		// instantly clear up space (help GC)
		map.get().clear();
		configuration.save();
	}

	@Deprecated
	public String getName() {
		return configuration.getName();
	}


	@Deprecated
	public Optional<String> getDescription() {
		return Optional.ofNullable(configuration.getDirectory());
	}

	@Deprecated
	public boolean delete() {
		return configuration.delete();
	}

	@Deprecated
	public boolean exists() {
		return configuration.exists();
	}

	@Deprecated
	public boolean create() throws IOException {
		return configuration.create();
	}

	@Deprecated
	public File getFile() {
		return configuration.getParent();
	}

	/**
	 * Get the FileConfiguration managed by this Config object.
	 *
	 * @return a File (Yaml) FileManager object
	 */
	@Deprecated
	synchronized public FileConfiguration getConfig() {
		return configuration instanceof YamlConfiguration ? ((YamlConfiguration) configuration).getConfig() : null;
	}

	@Deprecated
	synchronized public <R> R readValue(Function<FileConfiguration, R> function) {
		return function.apply(getConfig());
	}

	/**
	 * Get a Location from config safely (including legacy).
	 *
	 * @param node node of the location
	 * @return the stored location
	 */
	@Deprecated
	synchronized public @Nullable Location getLegacySafeLocation(String node) {
		return configuration.getLocation(node);
	}

	@Deprecated
	synchronized public void reload() {
		configuration.reload();
	}

	@Deprecated
	synchronized public void saveConfig() {
		configuration.save();
	}


	@Deprecated
	synchronized public void refreshConfig() {
		saveConfig();
		reload();
	}


	@Experimental("Still being tested")
	public @NotNull FileManager toJSON(String name, String dir) {
		FileManager n = FileList.search(plugin).find(name, dir, FileType.JSON);
		Configurable c = getChild();
		if (c instanceof YamlConfiguration) {
			DataMap inquiry = DataMap.newMap();
			for (String entry : c.getKeys(true)) {
				if (c.isNode(entry)) {
					ConfigurationSection s = c.getNode(entry).get(ConfigurationSection.class);
					for (String e : s.getKeys(false)) {
						if (s.isConfigurationSection(e)) {
							ConfigurationSection a = s.getConfigurationSection(e);
							inquiry.set(e, a.get(e));
						} else {
							inquiry.set(e, s.get(e));
						}
					}
				} else {
					inquiry.set(entry, c.getNode(entry).get());
				}
			}
			n.write(inquiry, false);
			return n;
		}
		return this;
	}

	@Experimental("Still being tested")
	public @NotNull FileManager toJSON() {
		String name = getChild().getName();
		String dir = getChild().getDirectory();
		FileManager n = FileList.search(plugin).find(name, dir, FileType.JSON);
		Configurable c = getChild();
		if (c instanceof YamlConfiguration) {
			DataMap inquiry = DataMap.newMap();
			for (String entry : c.getKeys(true)) {
				if (c.isNode(entry)) {
					ConfigurationSection s = c.getNode(entry).get(ConfigurationSection.class);
					for (String e : s.getKeys(false)) {
						if (s.isConfigurationSection(e)) {
							ConfigurationSection a = s.getConfigurationSection(e);
							inquiry.set(e, a.get(e));
						} else {
							inquiry.set(e, s.get(e));
						}
					}
				} else {
					inquiry.set(entry, c.getNode(entry).get());
				}
			}
			n.write(inquiry, false);
			return n;
		}
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FileManager)) return false;
		FileManager config = (FileManager) o;
		return configuration.equals(config.configuration);
	}

	@Override
	public int hashCode() {
		return configuration.hashCode();
	}
}

