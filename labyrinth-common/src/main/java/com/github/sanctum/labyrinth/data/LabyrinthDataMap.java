package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.task.Schedule;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Hempfest
 * @version 1.0
 */
public final class LabyrinthDataMap implements DataMap {

	private final Map<String, Object> tempSpace = new HashMap<>();

	@Override
	public <T> LabyrinthDataMap set(String key, T value) {
		if (value == null) {
			tempSpace.put(key, "NULL");
		} else {
			tempSpace.put(key, value);
		}
		return this;
	}

	@Override
	public DataMap removeAdded(Predicate<String> pred) {
		for (Map.Entry<String, Object> entry : get().entrySet()) {
			if (pred.test(entry.getKey())) {
				Schedule.sync(() -> tempSpace.remove(entry.getKey())).run();
				break;
			}
		}
		return this;
	}

	@Override
	public Map<String, Object> get() {
		return tempSpace;
	}


}
