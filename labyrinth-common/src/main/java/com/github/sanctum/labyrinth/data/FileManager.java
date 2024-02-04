package com.github.sanctum.labyrinth.data;

import com.github.sanctum.panther.annotation.Note;
import com.github.sanctum.panther.file.Configurable;
import com.github.sanctum.panther.file.JsonConfiguration;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
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

	protected FileManager(@NotNull Plugin plugin, @NotNull final String n, @Nullable final String d, Configurable.Extension data) {
		if (data.get().endsWith("data") || data.get().endsWith("json")) {
			JsonConfiguration c = new JsonConfiguration(plugin.getDataFolder(), n, d);
			this.plugin = plugin;
			this.configuration = c;
			configuration.register(new BukkitGeneric());
			return;
		}
		if (data.get().endsWith("yml")) {
			YamlConfiguration c = new YamlConfiguration(plugin, n, d);
			this.plugin = c.plugin;
			this.configuration = c;
			configuration.register(new BukkitGeneric());
			return;
		}
		throw new IllegalArgumentException(data.get() + " files cannot be instantiated through file manager, injection required!");
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
	 *
	 * @param table The data consumption to take place.
	 * @see FileManager#write(DataTable)
	 */
	public void write(Consumer<? super DataTable> table) {
		DataTable t = DataTable.newTable();
		table.accept(t);
		write(t);
	}

	/**
	 * Set/Replace & save multiple keyed value spaces within this file.
	 *
	 * @param table The data table to use when setting values.
	 * @see FileManager#write(DataTable, boolean)
	 */
	@Note("You can create a fresh DataTable really easily see DataTable#newTable()")
	public void write(@Note("Provided table gets cleared upon finalization.") DataTable table) {
		write(table, true);
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
	 * @param replace Whether to replace already set values from file with ones from the table
	 * @see FileManager#write(DataTable)
	 */
	@Note("You can create a fresh DataTable really easily see DataTable#newTable()")
	public void write(@Note("Provided table gets cleared upon finalization.") DataTable table, boolean replace) {
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
		// instantly clear up space (help GC, we don't need these elements anymore.)
		table.clear();
		configuration.save();
	}

	/**
	 * Copy all values from this yml file to a json file of similar stature.
	 *
	 * @param name The new name of the file.
	 * @param dir  The optional new directory, null places in base folder.
	 * @return a new json file containing all values from this yml file.
	 */
	public @NotNull FileManager toJSON(@NotNull String name, String dir) {
		FileManager n = FileList.search(plugin).get(name, dir, Configurable.Type.JSON);
		Configurable c = getRoot();
		if (c instanceof YamlConfiguration) {
			n.write(copy(), false);
			return n;
		}
		return this;
	}

	/**
	 * Copy all values from this json file to a yml file of similar stature.
	 *
	 * @param name The new name of the file.
	 * @param dir  The optional new directory, null places in base folder.
	 * @return a new yml file containing all values from this json file.
	 */
	public @NotNull FileManager toYaml(@NotNull String name, String dir) {
		FileManager n = FileList.search(plugin).get(name, dir, Configurable.Type.YAML);
		Configurable c = getRoot();
		if (c instanceof JsonConfiguration) {
			n.write(copy(), false);
			return n;
		}
		return this;
	}

	/**
	 * Copy all values from this yml file to a json file of similar stature.
	 *
	 * @return a new json file containing all values from this yml file.
	 */
	public @NotNull FileManager toJSON() {
		return toJSON(getRoot().getName(), getRoot().getDirectory());
	}

	/**
	 * Copy all values from this json file to a yml file of similar stature.
	 *
	 * @return a new yml file containing all values from this json file.
	 */
	public @NotNull FileManager toYaml() {
		return toYaml(getRoot().getName(), getRoot().getDirectory());
	}

	/**
	 * Move this file to another location. Retains all values but doesn't retain comments, only headers.
	 * *Automatically deletes old file when moved*
	 *
	 * @param dir The optional new directory to move to, null places in base folder.
	 * @return a new file containing all the values from this file.
	 */
	public @NotNull FileManager toMoved(String dir) {
		// gotta love our api sometimes, just look at how clean it is to copy ALL values from a config to another location.
		final FileManager n = FileList.search(plugin).get(getRoot().getName(), dir, getRoot().getType());
		Configurable c = getRoot();
		n.write(copy(), false);
		c.delete();
		return n;
	}

	/**
	 * This method assumes the resource being loaded is named and located no differently than this file managers local info and attempts to
	 * load the data from its source plugin jar.
	 *
	 * @return the same file manager instance.
	 */
	public @NotNull FileManager load() {
		if (!getRoot().exists()) {
			InputStream mainGrab = plugin.getResource(configuration.getName() + configuration.getType().get());
			if (mainGrab == null) throw new IllegalStateException("Unable to load " + '"' + configuration.getName() + configuration.getType().get() + '"' + " from plugin " + plugin.getName());
			FileList.copy(mainGrab, getRoot().getParent());
		}
		return this;
	}

	/**
	 * This method attempts to load a resource from the parent plugin under the specified root path.
	 *
	 * @param data the directory labeled file to load into this file manager.
	 * @return the same file manager instance.
	 */
	public @NotNull FileManager load(@NotNull String data) {
		if (!getRoot().exists()) {
			InputStream mainGrab = plugin.getResource(data);
			if (mainGrab == null) throw new IllegalStateException("Unable to load " + '"' + data + '"' + " from plugin " + plugin.getName());
			FileList.copy(mainGrab, getRoot().getParent());
		}
		return this;
	}

	/**
	 * Copy all contents to a datatable.
	 *
	 * @return a fresh datatable containing all values from this file.
	 */
	public @NotNull DataTable copy() {
		Configurable c = getRoot();
		DataTable inquiry = DataTable.newTable();
		c.getValues(true).forEach(inquiry::set);
		return inquiry;
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

