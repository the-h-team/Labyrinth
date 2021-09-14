package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.DataTable;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.library.HFEncoded;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Total safe encapsulation of serializable objects for persistent storage use.
 */
public class PersistentContainer extends PersistentData {

	private final NamespacedKey key;

	private final Map<String, Object> dataMap = new HashMap<>();

	private final Map<String, Boolean> persistenceMap = new HashMap<>();

	public PersistentContainer(NamespacedKey key) {
		this.key = key;
	}

	/**
	 * Check if a specified key value is present within the container.
	 *
	 * @param key the key delimiter for this value
	 * @return true if the specified key is found within the container
	 */
	@Override
	public synchronized boolean exists(String key) {
		if (!this.dataMap.containsKey(key)) {
			return found(key);
		}
		return true;
	}

	/**
	 * Check if a specified key value is stored persistently.
	 *
	 * @param key the key delimiter for the desired value
	 * @return true if the key value has been stored persistently
	 */
	protected synchronized boolean found(String key) {
		FileManager manager = FileList.search(LabyrinthProvider.getInstance().getPluginInstance()).get("Components", "Persistent");
		boolean f = manager.read(c -> c.isString(this.key.getNamespace() + "." + this.key.getKey() + "." + key));
		if (f && !this.dataMap.containsKey(key)) {
			try {
				Object o = new HFEncoded(manager.read(fi -> fi.getString(this.key.getNamespace() + "." + this.key.getKey() + "." + key))).deserialized();
				this.dataMap.put(key, o);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return f;
	}

	/**
	 * Delete the specified key's persisted data.
	 *
	 * @param key the key delimiter for the desired value to remove
	 * @return true if the persistence was successfully vanquished
	 */
	public synchronized boolean delete(String key) {
		this.dataMap.remove(key);
		FileManager manager = FileList.search(LabyrinthProvider.getInstance().getPluginInstance()).get("Components", "Persistent");
		if (manager.read(c -> c.isString(this.key.getNamespace() + "." + this.key.getKey() + "." + key))) {
			DataTable inquiry = DataTable.newTable();
			inquiry.set(this.key.getNamespace() + "." + this.key.getKey() + "." + key, null);
			manager.write(inquiry);
			final ConfigurationSection section = manager.read(c -> c.getNode(this.key.getNamespace() + "." + this.key.getKey()).get(ConfigurationSection.class));
			if (section != null && section.getKeys(false).isEmpty()) {
				inquiry.set(this.key.getNamespace() + "." + this.key.getKey(), null);
				manager.write(inquiry);
			}
			return true;
		}
		return false;
	}

	/**
	 * Save &amp; override any existing traces of the specified key value.
	 * <p>
	 * Will only save persisted values (such with
	 * {@link #attach(String, Object)}, but not with
	 * {@link #lend(String, Object)}.
	 *
	 * @param key the key delimiter of desired value to save
	 */
	public synchronized void save(String key) throws IOException {
		if (this.persistenceMap.get(key)) {
			FileManager manager = FileList.search(LabyrinthProvider.getInstance().getPluginInstance()).get("Components", "Persistent");
			DataTable inquiry = DataTable.newTable();
			inquiry.set(this.key.getNamespace() + "." + this.key.getKey() + "." + key, serialize(key));
			manager.write(inquiry);
		}
	}

	protected synchronized <R> R load(Class<R> type, String key) {
		FileManager manager = FileList.search(LabyrinthProvider.getInstance().getPluginInstance()).get("Components", "Persistent");
		if (manager.read(c -> c.isString(this.key.getNamespace() + "." + this.key.getKey() + "." + key))) {
			R value = deserialize(type, manager.read(f -> f.getString(this.key.getNamespace() + "." + this.key.getKey() + "." + key)));
			if (value == null) {
				delete(key);
				return null;
			}
			return attach(key, value);
		}
		return null;
	}

	/**
	 * Place a specified value into this persistent container under a specified key delimiter.
	 * <p>
	 * The persistence of this object will be attempted on shutdown.
	 *
	 * @param key the key delimiter for this value
	 * @param value the value to store
	 * @param <R> the type this value represents
	 * @return the stored value
	 */
	public <R> R attach(String key, R value) {
		this.dataMap.put(key, value);
		this.persistenceMap.put(key, true);
		return value;
	}

	/**
	 * Place a specified value into this persistent container under a specified key delimiter.
	 * <p>
	 * The persistence of this object will <strong>NOT</strong> be attempted on shutdown.
	 *
	 * @param key the key delimiter for this value
	 * @param value the value to store
	 * @param <R> the type this value represents
	 * @return the stored value
	 */
	public <R> R lend(String key, R value) {
		this.dataMap.put(key, value);
		this.persistenceMap.put(key, false);
		return value;
	}

	// TODO: Explicit throw of custom exception on get fail vs nullity contract
	/**
	 * Get a specified value by class type &amp; key delimiter.
	 *
	 * <p>If no value is found but a storage location is found, the class
	 * parameter will assist in both determining the final result of this
	 * method as well as loading found but not cached values into the container.
	 *
	 * @param type the type this value is assignable from
	 * @param key the key delimiter for this value
	 * @param <R> the type this value represents
	 * @return the desired persistent value otherwise null if not found or not
	 * assignable from the same class type
	 */
	@Override
	public <R> @Nullable R get(Class<R> type, String key) {
		if (!this.dataMap.containsKey(key)) {
			if (found(key)) {
				return load(type, key);
			}
			return null;
		}
		if (!type.isAssignableFrom(this.dataMap.get(key).getClass())) {
			return null;
		}
		return (R) this.dataMap.get(key);
	}

	/**
	 * Get all cached object keys within the container.
	 *
	 * @return all cached object keys
	 */
	@Override
	public Set<String> keySet() {
		return this.dataMap.keySet();
	}

	/**
	 * Get all object keys both cached &amp; non-cached.
	 *
	 * @return all object keys, period
	 */
	public synchronized List<String> persistentKeySet() {
		List<String> list = new LinkedList<>();
		FileManager manager = FileList.search(LabyrinthProvider.getInstance().getPluginInstance()).get("Components", "Persistent");
		if (manager.read(c -> c.isNode(this.key.getNamespace() + "." + this.key.getKey()))) {
			//noinspection ConstantConditions
			list.addAll(manager.read(f -> f.getNode(this.key.getNamespace() + "." + this.key.getKey()).getKeys(false)));
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
	 * @return all cached objects within the container
	 */
	public Collection<Object> values() {
		return this.dataMap.values();
	}

	/**
	 * Get all cached objects within this container of a specified type.
	 *
	 * @return all values of interest
	 */
	@Override
	public <R> Collection<? extends R> values(Class<R> type) {
		return this.dataMap.values().stream().filter(o -> type.isAssignableFrom(o.getClass())).map(o -> (R) o).collect(Collectors.toList());
	}

	/**
	 * Get the namespaced key for this container.
	 *
	 * @return this container's namespaced key
	 */
	public NamespacedKey getKey() {
		return this.key;
	}

	/**
	 * Deserialize an object of specified type from a string.
	 * <p>
	 * Primarily for misc use; deserialization is typically
	 * handled internally for normal object use from containers.
	 *
	 * @param type  the type this object represents
	 * @param value the serialized object to read
	 * @param <R>   the type this object represents
	 * @return the deserialized object otherwise null
	 */
	public <R> @Nullable R deserialize(Class<R> type, String value) {
		return new HFEncoded(value).deserialize(type);
	}

	// TODO: Explicit throw on serialization fail vs nullity contract
	/**
	 * Serialize a specified value from this container by its key delimiter.
	 *
	 * @param key the key delimiter for the value
	 * @return the serialized string otherwise null if an issue occurred
	 */
	public @Nullable String serialize(String key) throws IOException {
		return new HFEncoded(this.dataMap.get(key)).serialize();
	}


}
