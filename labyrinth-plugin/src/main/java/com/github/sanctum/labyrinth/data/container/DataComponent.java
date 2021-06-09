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

public class DataComponent {

	private final NamespacedKey NAME;

	private final Map<String, Object> DATA;

	public DataComponent(NamespacedKey key) {
		this.NAME = key;
		this.DATA = new HashMap<>();
	}

	public synchronized <R> boolean isLoaded(Class<R> type, String key) {
		if (!this.DATA.containsKey(key)) {
			if (exists(key)) {
				return load(type, key);
			}
		}
		return this.DATA.containsKey(key);
	}

	public synchronized boolean exists(String key) {
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		return manager.getConfig().isString(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key);
	}

	public synchronized void save(String key) {
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		manager.getConfig().set(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key, serialize(key));
		manager.saveConfig();
	}

	protected synchronized <R> boolean load(Class<R> type, String key) {
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		if (manager.getConfig().isString(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key)) {
			this.DATA.put(key, deserialize(type, manager.getConfig().getString(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key)));
			return true;
		}
		return false;
	}

	public synchronized boolean delete(String key) {
		FileManager manager = FileList.search(Labyrinth.getInstance()).find("Components", "Persistent");
		if (manager.getConfig().isString(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key)) {
			manager.getConfig().set(this.NAME.getNamespace() + "." + this.NAME.getKey() + "." + key, null);
			return true;
		}
		return false;
	}

	public <R> R attach(String key, R value) {
		this.DATA.put(key, value);
		return value;
	}

	public <R> R get(Class<R> type, String key) {
		if (!type.isAssignableFrom(this.DATA.get(key).getClass())) {
			return null;
		}
		return (R) this.DATA.get(key);
	}

	public Set<String> keySet() {
		return this.DATA.keySet();
	}

	public Collection<Object> values() {
		return this.DATA.values();
	}

	public NamespacedKey getKey() {
		return this.NAME;
	}

	public <R> R deserialize(Class<R> type, String value) {
		try {
			Object o = new HFEncoded(value).deserialized();
			if (o.getClass().isAssignableFrom(type)) {
				return (R) o;
			} else {
				throw new IllegalArgumentException(o.getClass().getSimpleName() + " is not assignable from " + type.getSimpleName());
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
