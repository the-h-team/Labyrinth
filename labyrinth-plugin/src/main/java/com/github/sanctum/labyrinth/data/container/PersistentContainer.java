package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.library.HFEncoded;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.NamespacedKey;

/**
 * Total safe encapsulation of serializable objects for persistent storage use.
 */
public class PersistentContainer extends PersistentData {

	private final NamespacedKey NAME;

	private final Map<String, Object> DATA;

	public PersistentContainer(NamespacedKey key) {
		this.NAME = key;
		this.DATA = new HashMap<>();
	}

	/**
	 * Check if a specified key value is present within the container.
	 *
	 * @param key The key delimiter for this value.
	 * @return true if the specified key is found within the container.
	 */
	@Override
	public synchronized boolean exists(String key) {
		if (!this.DATA.containsKey(key)) {
			return found(key);
		}
		return true;
	}

	/**
	 * Check if a specified key value found persistently.
	 *
	 * @param key The key delimiter for the desired value.
	 * @return true if the desired value found persistently within this container.
	 */
	protected synchronized boolean found(String key) {
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		return manager.getConfig().isString(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key);
	}

	/**
	 * Delete the specified key values persistence
	 *
	 * @param key The key delimiter for the desired value to remove.
	 * @return true if the persistence was successfully vanquished.
	 */
	public synchronized boolean delete(String key) {
		this.DATA.remove(key);
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		if (manager.getConfig().isString(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key)) {
			manager.getConfig().set(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key, null);
			manager.saveConfig();
			if (manager.getConfig().getConfigurationSection(this.NAME.getNamespace() + "." + this.NAME.getKey()).getKeys(false).isEmpty()) {
				manager.getConfig().set(this.NAME.getNamespace() + "." + this.NAME.getKey(), null);
				manager.saveConfig();
			}
			return true;
		}
		return false;
	}

	/**
	 * Save & override any existing traces of the specified key value.
	 *
	 * @param key The key delimiter for the desired value to save.
	 */
	public synchronized void save(String key) {
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		manager.getConfig().set(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key, serialize(key));
		manager.saveConfig();
	}

	protected synchronized <R> R load(Class<R> type, String key) {
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		if (manager.getConfig().isString(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key)) {
			R value = deserialize(type, manager.getConfig().getString(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key));
			this.DATA.put(key, value);
			return (R) this.DATA.get(key);
		}
		return null;
	}

	/**
	 * Place a specified value into this persistent container under a specified key delimiter.
	 *
	 * @param key   The key delimiter for this value.
	 * @param value The value to store.
	 * @param <R>   The type this value represents.
	 * @return The stored value.
	 */
	public <R> R attach(String key, R value) {
		this.DATA.put(key, value);
		return value;
	}

	/**
	 * Get a specified value by class type & key delimiter.
	 *
	 * <p>If no value is found but a storage location is found, the value will
	 * assist in both determining the final result of this method use
	 * as-well as loading found but not cached values into the container.
	 *
	 * @param type The type this value is assignable from.
	 * @param key  The key delimiter for this value.
	 * @param <R>  The type this value represents.
	 * @return The desired persistent value otherwise null if not found or not
	 * assignable from the same class type.
	 */
	@Override
	public <R> R get(Class<R> type, String key) {
		if (!this.DATA.containsKey(key)) {
			if (found(key)) {
				return load(type, key);
			}
			return null;
		}
		if (!type.isAssignableFrom(this.DATA.get(key).getClass())) {
			return null;
		}
		return (R) this.DATA.get(key);
	}

	/**
	 * Get all cached object key's within the container.
	 *
	 * @return All cached object key's
	 */
	@Override
	public Set<String> keySet() {
		return this.DATA.keySet();
	}

	/**
	 * Get all object key's both cached & non-cached
	 *
	 * @return All object key's period.
	 */
	public synchronized List<String> persistentKeySet() {
		List<String> list = new LinkedList<>();
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		if (manager.getConfig().isConfigurationSection(this.NAME.getNamespace() + "." + this.NAME.getKey())) {
			list.addAll(manager.getConfig().getConfigurationSection(this.NAME.getNamespace() + "." + this.NAME.getKey()).getKeys(false));
		}
		for (String cached : keySet()) {
			if (!list.contains(cached)) {
				list.add(cached);
			}
		}
		return list;
	}

	/**
	 * Get all cached objects within this container.
	 *
	 * @return All cached objects within the container.
	 */
	public Collection<Object> values() {
		return this.DATA.values();
	}

	/**
	 * Get all cached objects within this container of a specified type.
	 *
	 * @return All values of interest.
	 */
	@Override
	public <R> Collection<? extends R> values(Class<R> type) {
		return this.DATA.values().stream().filter(o -> o.getClass().isAssignableFrom(type)).map(o -> (R) o).collect(Collectors.toList());
	}

	/**
	 * Get the name space for this container.
	 *
	 * @return The container's name space
	 */
	public NamespacedKey getKey() {
		return this.NAME;
	}

	/**
	 * Deserialize an object of specified type from a string.
	 * <p>
	 * Primarily for misc use, deserialization is handled internally for normal object use from containers.
	 *
	 * @param type  The type this object represents.
	 * @param value The serialized object to deserialize
	 * @param <R>   The type this object represents
	 * @return The deserialized object otherwise null.
	 */
	public <R> R deserialize(Class<R> type, String value) {
		return new HFEncoded(value).deserialize(type);
	}

	/**
	 * Serialize a specified value from this container by its key delimiter
	 *
	 * @param key The key delimiter for the value.
	 * @return The serialized string otherwise null if an issue occurred.
	 */
	public String serialize(String key) {
		try {
			return new HFEncoded(this.DATA.get(key)).serialize();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


}
