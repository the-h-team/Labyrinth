package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.library.HFEncoded;
import com.github.sanctum.labyrinth.library.HUID;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;

public class DataContainer extends DataStream implements Serializable {

	private static final Map<HUID, DataStream> metaDataContainer = new HashMap<>();

	private final String metaId;

	private final HUID huid;

	private String value;

	private final DataContainer instance;

	private static boolean debugging;

	private final List<String> values = new ArrayList<>();

	/**
	 * @param metaId The id to save the meta under.
	 */
	public DataContainer(String metaId) {
		this.metaId = metaId;
		instance = this;
		huid = HUID.randomID();
	}

	/**
	 * Start construction on a new data stream.
	 *
	 * @param metaId The unique id for this persistent container. ONLY [_-] chars allowed.
	 * @return An {@link DataStream} builder.
	 */
	public static DataContainer build(String metaId) {
		return new DataContainer(metaId);
	}

	@Override
	public HUID getId() {
		return huid;
	}

	@Override
	public String value() {
		return value;
	}

	@Override
	public String value(int index) {
		return values.get(index);
	}

	@Override
	public String getMetaId() {
		return metaId;
	}

	/**
	 * Save any specified object to the meta data.
	 *
	 * @param o The object data to be stored within the container.
	 */
	public void setValue(Object o) {
		try {
			this.value = new HFEncoded(o).serialize();
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe("Unable to parse object.");
			e.printStackTrace();
		}
	}

	/**
	 * Save any specified object to the meta data large container.
	 *
	 * @param o     The object to insert
	 * @param index Insert an object into the value array at the given position.
	 */
	public void setValue(Object o, int index) {
		try {
			this.values.add(index, new HFEncoded(o).serialize());
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe("Unable to parse object.");
			e.printStackTrace();
		}
	}

	/**
	 * Get all currently loaded meta id's
	 *
	 * @return Gets an array of all loaded data id's
	 */
	public static HUID[] get() {
		List<HUID> array = new ArrayList<>();
		for (Map.Entry<HUID, DataStream> entry : metaDataContainer.entrySet()) {
			array.add(entry.getKey());
		}
		return array.toArray(new HUID[0]);
	}

	/**
	 * @param b true = console information displays every action used.
	 */
	public void setDebugging(boolean b) {
		debugging = b;
	}

	/**
	 * @return A persistent data id by set delimiter
	 */
	public static HUID getHuid(String metaId) {
		FileManager data = FileList.search(Labyrinth.getInstance()).find("Meta", "Persistent");
		HUID result = null;
		try {
			for (String d : data.getConfig().getConfigurationSection("Data").getKeys(false)) {
				if (data.getConfig().getString("Data." + d).equals("core:" + metaId)) {
					result = HUID.fromString(d);
				}
			}
		} catch (NullPointerException ignored) {
		}
		return result;
	}

	/**
	 * Load an instance of the same meta data but allocate new values to it.
	 *
	 * @param huid    The id to load from cache
	 * @param persist if temp result null persist into hard storage?
	 * @return Gets a data stream builder retaining the values of the old container.
	 * @throws IllegalAccessException If the stream is persistent and access to it was denied or non-existent.
	 */
	public static DataContainer reload(HUID huid, boolean persist) throws IllegalAccessException {
		DataStream stream = loadInstance(huid, persist);
		if (stream != null) {
			DataContainer c = (DataContainer) stream;
			DataContainer move = build(c.metaId);
			move.values.addAll(c.values);
			deleteInstance(c.getId());
			move.storeTemp();
			move.saveMeta();
			return move;
		}
		throw new IllegalAccessException("Access to this data stream was denied or the parent location is non-existent.");
	}

	/**
	 * Load an instance of meta data from cache
	 *
	 * @param huid    The id to load from cache
	 * @param persist if temp result null persist into hard storage?
	 * @return Gets a cached data instance.
	 */
	public static DataStream loadInstance(HUID huid, boolean persist) {
		DataStream meta = null;
		for (HUID entry : get()) {
			if (entry.equals(huid)) {
				meta = metaDataContainer.get(entry);
			}
		}
		if (meta == null) {
			if (persist) {
				FileManager file = FileList.search(Labyrinth.getInstance()).find(huid.toString(), "Container");
				DataContainer dataContainer = null;
				if (file.exists()) {
					try {
						DataContainer instance = (DataContainer) new HFEncoded(file.getConfig().getString("Data")).deserialized();
						metaDataContainer.put(huid, instance);
						dataContainer = instance;
					} catch (IOException | ClassNotFoundException e) {
						Bukkit.getServer().getLogger().severe("[Labyrinth] - Instance not loadable. One or more values changed or object location changed.");
						e.printStackTrace();
					}
				} else {
					Bukkit.getServer().getLogger().severe("[Labyrinth] - No saved meta data can be found. Are you sure you saved it?");
				}
				if (dataContainer == null) {
					Bukkit.getServer().getLogger().severe("[Labyrinth] - Failed attempt at loading non existent instance of HUID link");
				} else {
					meta = dataContainer;
				}
			}
		}

		return meta;
	}

	/**
	 * Delete an instance of meta data from both cache and hard storage.
	 *
	 * @param huid The id to delete from cache/storage
	 */
	public static void deleteInstance(HUID huid) {
		Arrays.stream(get()).forEach(I -> {
			if (I.toString().equals(huid.toString())) {
				FileManager data = FileList.search(Labyrinth.getInstance()).find("Meta", "Persistent");
				if (!data.getConfig().isConfigurationSection("Data")) {
					throw new NullPointerException("[Labyrinth] - No data is currently saved.");
				}
				for (String d : data.getConfig().getConfigurationSection("Data").getKeys(false)) {
					if (d.equals(huid.toString())) {
						data.getConfig().set("Data." + d, null);
						data.saveConfig();
						break;
					}
				}
				FileManager meta = FileList.search(Labyrinth.getInstance()).find(I.toString(), "Container");
				meta.delete();
				if (debugging) {
					Bukkit.getServer().getLogger().info("[Labyrinth] - Instance for ID #" + I.toString() + " deleted.");
				}
				metaDataContainer.remove(I);
			}
		});
	}

	/**
	 * Load all storage saved clan meta into cache,
	 * this should not be used as it is already logged on server enable.
	 */
	public static void querySaved() {
		final File dir = Labyrinth.getInstance().getDataFolder();
		File file = new File(dir.getParentFile().getPath(), Labyrinth.getInstance().getName() + "/Container/");
		Arrays.stream(file.listFiles()).forEach(f -> {
			HUID id = HUID.fromString(f.getName().replace(".yml", ""));
			DataContainer m = loadSavedInstance(id);
			metaDataContainer.put(id, m);
		});
	}


	private static DataContainer loadSavedInstance(HUID huid) {
		FileManager file = FileList.search(Labyrinth.getInstance()).find(huid.toString(), "Container");
		DataContainer dataContainer = null;
		if (file.exists()) {
			try {
				DataContainer instance = (DataContainer) new HFEncoded(file.getConfig().getString("Data")).deserialized();
				metaDataContainer.put(huid, instance);
				dataContainer = instance;
			} catch (IOException | ClassNotFoundException e) {
				Bukkit.getServer().getLogger().severe("[Labyrinth] - Instance not loadable. One or more values changed or object location changed.");
				e.printStackTrace();
			}
		} else {
			Bukkit.getServer().getLogger().severe("[Labyrinth] - No saved meta data can be found. Are you sure you saved it?");
		}
		if (dataContainer == null) {
			Bukkit.getServer().getLogger().severe("[Labyrinth] - Failed attempt at loading non existent instance of HUID link");
		}
		return dataContainer;
	}

	/**
	 * Store the clan meta into temp storage.
	 */
	public void storeTemp() {
		metaDataContainer.put(huid, instance);
		if (debugging) {
			Bukkit.getServer().getLogger().info("[Labyrinth] - Instance for ID #" + instance.huid.toString() + " cached.");
		}
	}

	/**
	 * Store the data into hard storage under your specified suffix
	 */
	public void saveMeta() {
		FileManager data = FileList.search(Labyrinth.getInstance()).find("Meta", "Persistent");
		data.getConfig().set("Data." + instance.huid.toString(), "core:" + metaId);
		data.saveConfig();
		FileManager meta = FileList.search(Labyrinth.getInstance()).find(huid.toString(), "Container");
		try {
			meta.getConfig().set("Data", new HFEncoded(instance).serialize());
			meta.saveConfig();
			if (debugging) {
				Bukkit.getServer().getLogger().info("[Labyrinth] - Instance for ID #" + instance.huid.toString() + " saved.");
				if (value != null) {
					Bukkit.getServer().getLogger().info("[Labyrinth] - Object value for ID #" + instance.huid.toString() + " saved.");
				}
			}
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe("[Labyrinth] - Unable to parse object.");
			e.printStackTrace();
		}
	}


}
