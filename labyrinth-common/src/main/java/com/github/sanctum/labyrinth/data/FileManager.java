package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.annotation.Experimental;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
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

	protected FileManager(@NotNull Plugin plugin, @NotNull final String n, @Nullable final String d, FileExtension data) {
		if (data.getExtension().endsWith("data")) {
			JsonConfiguration c = new JsonConfiguration(plugin, null, n, d);
			this.plugin = c.plugin;
			this.configuration = c;
			return;
		}
		if (data.getExtension().endsWith("yml")) {
			YamlConfiguration c = new YamlConfiguration(plugin, n, d);
			this.plugin = c.plugin;
			this.configuration = c;
			return;
		}
		throw new IllegalArgumentException(data.getExtension() + " files cannot be instantiated through file manager, injection required!");
	}

	/**
	 * Get the configurable root for this file manager.
	 *
	 * @return The configurable root for this manager.
	 */
	public Configurable getRoot() {
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
	 * A functional delegation to data table consumption
	 * @see FileManager#write(DataTable)
	 *
	 * @param table The data consumption to take place.
	 */
	public void write(Consumer<? super DataTable> table) {
		DataTable t = DataTable.newTable();
		table.accept(t);
		write(t);
	}

	/**
	 * Set & save multiple keyed value spaces within this file.
	 *
	 * <p>After all inquires have been transferred the inquiry object is cleared and discarded due to
	 * being of no further importance.</p>
	 *
	 * <p>Takes all 2d declarations and forms them into multi-layered nodes.</p>
	 *
	 * <p>By default this method is set to override any already existing nodes store
	 * within the configurable</p>
	 *
	 * @param table The data table to use when setting values.
	 */
	@Note("You can create a fresh DataTable really easily see DataTable#newTable()")
	public void write(@Note("Custom implementations will work here!") DataTable table) {
		write(table, true);
	}

	/**
	 * @param replace Whether to replace already set values from file with ones from the table
	 * @see FileManager#write(DataTable)
	 */
	@Note("You can create a fresh DataTable really easily see DataTable#newTable()")
	public void write(@Note("Custom implementations will work here!") DataTable table, boolean replace) {
		for (Map.Entry<String, Object> entry : table.values().entrySet()) {
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
		table.clear();
		configuration.save();
	}

	public @NotNull FileManager toJSON(String name, String dir) {
		FileManager n = FileList.search(plugin).get(name, dir, FileType.JSON);
		Configurable c = getRoot();
		if (c instanceof YamlConfiguration) {
			DataTable inquiry = DataTable.newTable();
			for (String entry : c.getKeys(true)) {
				if (c.isNode(entry)) {
					ConfigurationSection s = c.getNode(entry).get(ConfigurationSection.class);
					for (String e : s.getKeys(true)) {
						if (s.isConfigurationSection(e)) {
							ConfigurationSection a = s.getConfigurationSection(e);
							inquiry.set(e, a.get(e));
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

	public @NotNull FileManager toYaml(String name, String dir) {
		FileManager n = FileList.search(plugin).get(name, dir, FileType.JSON);
		Configurable c = getRoot();
		if (c instanceof JsonConfiguration) {
			DataTable inquiry = DataTable.newTable();
			for (String entry : c.getKeys(true)) {
				if (c.isNode(entry)) {
					Node s = c.getNode(entry);
					for (String e : s.getKeys(true)) {
						if (s.isNode(e)) {
							Node a = s.getNode(e);
							inquiry.set(e, a.get());
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

	public @NotNull FileManager toJSON() {
		return toJSON(getRoot().getName(), getRoot().getDirectory());
	}

	public @NotNull FileManager toYaml() {
		return toYaml(getRoot().getName(), getRoot().getDirectory());
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

