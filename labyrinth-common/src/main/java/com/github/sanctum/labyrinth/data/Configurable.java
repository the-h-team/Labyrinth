package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.NodePointer;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.service.AnnotationDiscovery;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A utility reserved for delegating either {@link FileType#JSON} files or {@link FileType#YAML} files into a singular abstraction for familiar use.
 * @author Hempfest
 * @version 1.0
 */
public abstract class Configurable implements MemorySpace, Removable {

	protected static final Map<String, JsonAdapterContext<?>> serializers = new HashMap<>();
	protected final Set<Node> nodes = new HashSet<>();


	/**
	 * Register a json element adapter for automatic use with json data usage.
	 *
	 * <p>All element adapters require a {@link NodePointer} annotation to resolve types when deserializing.
	 * If a pointer is not present this adapter cannot properly register.</p>
	 *
	 * <p>If the provided {@link NodePointer} doesn't equate to the exact package location of the service for serialization
	 * this will also cause issue when attempting deserialization.</p>
	 *
	 * <p>All {@link JsonAdapter} object's require a bare accessible constructor. In the event of one missing issue may occur.</p>
	 *
	 * @param c The adapter to register.
	 */
	public static void registerClass(@NotNull Class<? extends JsonAdapter<?>> c) {
		try {
			JsonAdapter<?> d = c.getDeclaredConstructor().newInstance();
			String key = AnnotationDiscovery.of(NodePointer.class, d).map((r, u) -> r.value());
			if (key == null)
				throw new RuntimeException("NodePointer annotation missing, JSON object serialization requires it.");
			serializers.put(key, new JsonAdapterContext.Impl<>(d));
		} catch (Exception e) {
			LabyrinthProvider.getService(Service.MESSENGER).getNewMessage().error("Class " + c.getSimpleName() + " failed to register JSON serialization handlers.");
			e.printStackTrace();
		}
	}

	/**
	 * Search for the desired element adapter for quick use.
	 *
	 * @param type The type of adapter to get.
	 * @param <T> The adapter type.
	 * @return The desired Json element adapter or null if non existent.
	 */
	public static <V> @Nullable JsonAdapter<V> getAdapter(@NotNull Class<V> type) {
		return serializers.entrySet().stream().filter(e -> e.getKey().equals(type.getName())).map(Map.Entry::getValue).map(c -> (JsonAdapter<V>)c).findFirst().orElse(null);
	}

	protected abstract Object get(String key);

	protected abstract <T> T get(String key, Class<T> type);

	/**
	 * Store an object under a specified path. Any current relative information to the path will be over-written.
	 *
	 * @param key The path to save the object under.
	 * @param o The object to store.
	 */
	public abstract void set(String key, Object o);

	/**
	 * Get a location.
	 *
	 * @param key The path the location resides under.
	 * @return The location or null
	 */
	public abstract Location getLocation(String key);

	/**
	 * Get an itemstack.
	 *
	 * @param key The path the itemstack resides under.
	 * @return The itemstack or null
	 */
	public abstract ItemStack getItemStack(String key);

	/**
	 * Get a string.
	 *
	 * @param key The path the string resides under.
	 * @return The string or null.
	 */
	public abstract String getString(String key);

	/**
	 * Get a boolean.
	 *
	 * @param key The path the boolean resides under.
	 * @return The boolean or false
	 */
	public abstract boolean getBoolean(String key);

	/**
	 * Get a double.
	 *
	 * @param key The path the double resides under.
	 * @return The string or 0.0.
	 */
	public abstract double getDouble(String key);

	/**
	 * Get a long.
	 *
	 * @param key The path the long resides under.
	 * @return The long or 0L
	 */
	public abstract long getLong(String key);

	/**
	 * Get a float.
	 *
	 * @param key The path the float resides under.
	 * @return The float or 0.0f
	 */
	public abstract float getFloat(String key);

	/**
	 * Get an integer.
	 *
	 * @param key The path the integer resides under.
	 * @return The integer or 0
	 */
	public abstract int getInt(String key);

	/**
	 * Get a map.
	 *
	 * @param key The path the map resides under.
	 * @return The map or a new empty one.
	 */
	public abstract Map<?, ?> getMap(String key);

	/**
	 * Get a list.
	 *
	 * @param key The path the list resides under.
	 * @return The list or a new empty one.
	 */
	public abstract List<?> getList(String key);

	/**
	 * Get a string list.
	 *
	 * @param key The path the string list resides under.
	 * @return The string list or a new empty one.
	 */
	public abstract List<String> getStringList(String key);

	/**
	 * Get an integer list.
	 *
	 * @param key The path the integer list resides under.
	 * @return The integer list or a new empty one.
	 */
	public abstract List<Integer> getIntegerList(String key);

	/**
	 * Get a double list.
	 *
	 * @param key The path the double list resides under.
	 * @return The double list or a new empty one.
	 */
	public abstract List<Double> getDoubleList(String key);

	/**
	 * Get a float list.
	 *
	 * @param key The path the float list resides under.
	 * @return The float list or a new empty one.
	 */
	public abstract List<Float> getFloatList(String key);

	/**
	 * Get a long list.
	 *
	 * @param key The path the long list resides under.
	 * @return The long list or a new empty one.
	 */
	public abstract List<Long> getLongList(String key);

	/**
	 * Check if a location resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if a location was found.
	 */
	public abstract boolean isLocation(String key);

	/**
	 * Check if a list resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if a list was found.
	 */
	public abstract boolean isList(String key);

	/**
	 * Check if a string list resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if a string list was found.
	 */
	public abstract boolean isStringList(String key);

	/**
	 * Check if a float list resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if a float list was found.
	 */
	public abstract boolean isFloatList(String key);

	/**
	 * Check if a double list resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if a double list was found.
	 */
	public abstract boolean isDoubleList(String key);

	/**
	 * Check if a long list resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if a long list was found.
	 */
	public abstract boolean isLongList(String key);

	/**
	 * Check if an integer list resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if an integer list was found.
	 */
	public abstract boolean isIntegerList(String key);

	/**
	 * Check if an itemstack resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if an itemstack was found.
	 */
	public abstract boolean isItemStack(String key);

	/**
	 * Check if a boolean resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if a boolean was found.
	 */
	public abstract boolean isBoolean(String key);

	/**
	 * Check if a double resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if a double was found.
	 */
	public abstract boolean isDouble(String key);

	/**
	 * Check if an int resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if an integer was found.
	 */
	public abstract boolean isInt(String key);

	/**
	 * Check if a long resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if a long was found.
	 */
	public abstract boolean isLong(String key);

	/**
	 * Check if a float resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if a float was found.
	 */
	public abstract boolean isFloat(String key);

	/**
	 * Check if a string resides within a specified path.
	 *
	 * @param key The path to check.
	 * @return true if a string was found.
	 */
	public abstract boolean isString(String key);

	/**
	 * Reload the file from disk.
	 * <p>
	 * If the backing file has been deleted, this method assigns a fresh,
	 * blank configuration internally to this object. Otherwise, the file
	 * is read from, directly replacing the existing configuration with
	 * its values. No attempt is made to save the existing configuration
	 * state, so keep that in mind when running this call.
	 */
	public abstract void reload();

	/**
	 * Attempt creating the file location.
	 * <p>
	 * If the parent location doesn't exist (The backing location for our file)
	 * One will be created before attempting file creation.
	 *
	 * @return true if creation was successful
	 */
	public abstract boolean create() throws IOException;

	/**
	 * Check if the backing file is currently existent.
	 * <p>
	 * Does not interact whatsoever with the internal YamlConfiguration.
	 *
	 * @return true if file found
	 */
	public abstract boolean exists();

	/**
	 * Get the name of this Config.
	 *
	 * @return name of Config
	 */
	public abstract String getName();

	/**
	 * Get the description of the config if it has one.
	 * <p>
	 * Used to resolve subdirectory if present.
	 *
	 * @return this config's sub-directory
	 */
	public abstract String getDirectory();

	/**
	 * Get the backing file for this Config.
	 * <p>
	 * A mandatory {@link FileManager#exists()} check should also be used before
	 * accessing a file directly following the {@link FileManager#create()} method.
	 *
	 * @return backing file File object
	 */
	public abstract File getParent();

	/**
	 * @return The type this file represents.
	 */
	public FileType getType() {
		return FileType.UNKNOWN;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Configurable) {
			Configurable c = (Configurable) obj;
			return Objects.equals(getName(), c.getName()) &&
					Objects.equals(getDirectory(), c.getDirectory());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getDirectory());
	}
}
