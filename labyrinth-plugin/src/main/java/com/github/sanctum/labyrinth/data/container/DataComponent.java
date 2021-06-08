package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.library.HFEncoded;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.NamespacedKey;

public class DataComponent<V> {

	private final Class<V> TYPE;

	private final NamespacedKey NAME;

	private final Map<String, V> DATA;

	public DataComponent(Class<V> path, NamespacedKey key) {
		this.NAME = key;
		this.TYPE = path;
		this.DATA = new HashMap<>();
	}

	public DataComponent<V> attach(String key, V value) {
		this.DATA.put(key, value);
		return this;
	}

	public synchronized boolean isLoaded(String key) {
		if (!this.DATA.containsKey(key)) {
			if (exists(key)) {
				return load(key);
			}
		}
		return this.DATA.containsKey(key);
	}

	public synchronized boolean exists(String key) {
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		return manager.getConfig().isString(key);
	}

	public synchronized void save(String key) {
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		manager.getConfig().set(key, serialize(key));
		manager.saveConfig();
	}

	protected synchronized boolean load(String key) {
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		if (manager.getConfig().isString(key)) {
			this.DATA.put(key, deserialize(manager.getConfig().getString(key)));
			return true;
		}
		return false;
	}

	public synchronized boolean delete(String key) {
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		if (manager.getConfig().isString(key)) {
			manager.getConfig().set(key, null);
			return true;
		}
		return false;
	}

	public V get(String key) {
		return this.DATA.get(key);
	}

	public Set<String> keySet() {
		return this.DATA.keySet();
	}

	public Collection<V> values() {
		return this.DATA.values();
	}

	public Class<V> getPrimative() {
		return TYPE;
	}

	public NamespacedKey getKey() {
		return this.NAME;
	}

	public V deserialize(String value) {
		try {
			Object o = new HFEncoded(value).deserialized();
			if (o.getClass().isAssignableFrom(this.TYPE)) {
				return (V) o;
			} else {
				throw new IllegalArgumentException(o.getClass().getSimpleName() + " is not assignable from " + this.TYPE.getSimpleName());
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String serialize(String key) {
		try {
			return new HFEncoded(this.DATA.get(key)).serialize();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


}
