package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * @author Hempfest
 * @version 1.0
 */
public class YamlConfiguration extends Configurable {

	protected final String n;
	protected final String d;
	protected final File file;
	protected FileConfiguration fc;
	protected final File parent;
	protected final Plugin plugin;

	protected YamlConfiguration(Plugin plugin, String n, String d) {
		this.n = n;
		this.d = d;
		this.plugin = plugin;
		// Get the data directory of the plugin that is providing this Config implementation
		final File pluginDataDir = plugin.getDataFolder();
		if (!pluginDataDir.exists()) {
			// If no primary plugin folder is found, create it.
			//noinspection ResultOfMethodCallIgnored
			pluginDataDir.mkdir();
		}
		// If d is null or empty, use plugin's data folder. If present get the file describing the subdirectory.
		final File parent = (d == null || d.isEmpty()) ? pluginDataDir : new File(pluginDataDir, d);
		if (!parent.exists()) {
			//noinspection ResultOfMethodCallIgnored
			parent.mkdir();
		}
		this.parent = parent;
		this.file = new File(parent, n.concat(".yml"));
	}

	synchronized protected FileConfiguration getConfig() {
		if (this.fc == null) {
			// fast exit with new blank configuration in the case of nonexistent file
			if (!file.exists()) {
				org.bukkit.configuration.file.YamlConfiguration result = new org.bukkit.configuration.file.YamlConfiguration();
				this.fc = result;
				return result;
			}
			// attach configuration from file
			this.fc = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
		}
		return this.fc;
	}

	@Override
	public void reload() {
		if (!this.file.exists()) {
			this.fc = new org.bukkit.configuration.file.YamlConfiguration();
		}

		this.fc = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(this.file);
		File defConfigStream = new File(this.plugin.getDataFolder(), getName() + ".yml");
		org.bukkit.configuration.file.YamlConfiguration defConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(defConfigStream);
		this.fc.setDefaults(defConfig);
	}

	@Override
	public boolean save() {
		try {
			getConfig().save(file);
			return true;
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to save configuration file!", e);
		}
	}

	@Override
	public boolean delete() {
		return file.delete();
	}

	@Override
	public boolean create() throws IOException {
		return parent.exists() ? file.createNewFile() : parent.mkdir() && file.createNewFile();
	}

	@Override
	public boolean exists() {
		return parent.exists() && file.exists();
	}

	@Override
	public String getName() {
		return this.n;
	}

	@Override
	public String getDirectory() {
		return this.d;
	}

	@Override
	public File getParent() {
		return file;
	}

	@Override
	public void set(String key, Object o) {
		getConfig().set(key, o);
	}

	@Override
	public Object get(String key) {
		return getConfig().get(key);
	}

	@Override
	public <T> T get(String key, Class<T> type) {
		Object o = get(key);
		if (o == null) return null;
		if (!o.getClass().isAssignableFrom(type)) return null;
		return (T) o;
	}

	@Override
	public Node getNode(String key) {
		return (Node) memory.entrySet().stream().filter(n -> n.getKey().equals(key)).map(Map.Entry::getValue).findFirst().orElseGet(() -> {
			ConfigurableNode n = new ConfigurableNode(key, this);
			memory.put(n.getPath(), n);
			return n;
		});
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		return getConfig().getKeys(deep);
	}

	@Override
	public Map<String, Object> getValues(boolean deep) {
		return getConfig().getValues(deep);
	}

	@Override
	public Location getLocation(String key) {
		if (LabyrinthProvider.getInstance().requiresLocationLibrary()) {
			final Object o = getConfig().get(key);
			if (!(o instanceof LegacyConfigLocation)) return null;
			return ((LegacyConfigLocation) o).getLocation();
		}
		return getConfig().getLocation(key);
	}

	@Override
	public ItemStack getItemStack(String key) {
		return getConfig().getItemStack(key);
	}

	@Override
	public String getString(String key) {
		return getConfig().getString(key);
	}

	@Override
	public boolean getBoolean(String key) {
		return getConfig().getBoolean(key);
	}

	@Override
	public boolean isLocation(String key) {
		return getLocation(key) != null;
	}

	@Override
	public boolean isList(String key) {
		return getConfig().isList(key);
	}

	@Override
	public boolean isStringList(String key) {
		return !getConfig().getStringList(key).isEmpty();
	}

	@Override
	public boolean isFloatList(String key) {
		return !getConfig().getFloatList(key).isEmpty();
	}

	@Override
	public boolean isDoubleList(String key) {
		return !getConfig().getDoubleList(key).isEmpty();
	}

	@Override
	public boolean isLongList(String key) {
		return !getConfig().getLongList(key).isEmpty();
	}

	@Override
	public boolean isIntegerList(String key) {
		return !getConfig().getIntegerList(key).isEmpty();
	}

	@Override
	public boolean isItemStack(String key) {
		return getConfig().isItemStack(key);
	}

	@Override
	public boolean isBoolean(String key) {
		return getConfig().isBoolean(key);
	}

	@Override
	public boolean isDouble(String key) {
		return getConfig().isDouble(key);
	}

	@Override
	public boolean isInt(String key) {
		return getConfig().isInt(key);
	}

	@Override
	public boolean isLong(String key) {
		return getConfig().isLong(key);
	}

	@Override
	public boolean isFloat(String key) {
		return get(key) instanceof Float;
	}

	@Override
	public boolean isString(String key) {
		return getConfig().isString(key);
	}

	@Override
	public String getPath() {
		String s = "/" + getName() + "/";
		if (getDirectory() != null) {
			s = s + getDirectory();
		}
		return s;
	}

	@Override
	public boolean isNode(String key) {
		return getConfig().isConfigurationSection(key);
	}

	@Override
	public double getDouble(String key) {
		return getConfig().getDouble(key);
	}

	@Override
	public long getLong(String key) {
		return getConfig().getLong(key);
	}

	@Override
	public float getFloat(String key) {
		return isFloat(key) ? Float.parseFloat((String) getConfig().get(key)) : 0;
	}

	@Override
	public int getInt(String key) {
		return getConfig().getInt(key);
	}

	@Override
	public Map<?, ?> getMap(String key) {
		return (Map<?, ?>) getConfig().get(key);
	}

	@Override
	public List<?> getList(String key) {
		return getConfig().getList(key);
	}

	@Override
	public List<String> getStringList(String key) {
		return getConfig().getStringList(key);
	}

	@Override
	public List<Integer> getIntegerList(String key) {
		return getConfig().getIntegerList(key);
	}

	@Override
	public List<Double> getDoubleList(String key) {
		return getConfig().getDoubleList(key);
	}

	@Override
	public List<Float> getFloatList(String key) {
		return getConfig().getFloatList(key);
	}

	@Override
	public List<Long> getLongList(String key) {
		return getConfig().getLongList(key);
	}

	@Override
	public FileType getType() {
		return FileType.YAML;
	}

}
