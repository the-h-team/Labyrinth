package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.library.HUID;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PersistentData {

	/**
	 * Load an instance of the same meta data but allocate new values to it.
	 *
	 * @param huid    The id to attach from cache
	 * @param persist if temp result null persist into hard storage?
	 * @return Gets a data stream builder retaining the values of the old container.
	 * @throws IllegalAccessException If the stream is persistent and access to it was denied or non-existent.
	 */
	public static DataContainer reload(HUID huid, boolean persist) throws IllegalAccessException, NoSuchFieldException {
		DataStream stream = DataContainer.loadInstance(huid, persist);
		if (stream != null) {
			DataContainer c = (DataContainer) stream;
			Field id = c.getClass().getDeclaredField("metaId");
			Field value = c.getClass().getDeclaredField("value");
			Field values = c.getClass().getDeclaredField("values");
			value.setAccessible(true);
			values.setAccessible(true);
			id.setAccessible(true);
			String v1 = (String) value.get(c);
			List<String> v2 = (List<String>) values.get(c);
			DataContainer move = build((String) id.get(c));
			Field values2 = move.getClass().getDeclaredField("values");
			List<String> current = new ArrayList<>((List<String>) values2.get(move));
			current.addAll(v2);
			values2.set(move, current);
			move.setValue(v1);
			DataContainer.deleteInstance(c.getId());
			move.storeTemp();
			move.saveMeta();
			return move;
		}
		throw new IllegalAccessException("Access to this data stream was denied or the parent location is non-existent.");
	}

	public static DataContainer build(String metaId) {
		return new DataContainer(metaId);
	}

}
