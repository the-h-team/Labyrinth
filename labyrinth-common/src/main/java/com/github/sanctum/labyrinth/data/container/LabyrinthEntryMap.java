package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.data.ReplaceableKeyedValue;
import java.util.Map;

public final class LabyrinthEntryMap<K, V> extends LabyrinthMapBase<K, V> {

	public LabyrinthEntryMap() {
		super();
	}

	public LabyrinthEntryMap(int capacity) {
		super(capacity);
	}

	public LabyrinthEntryMap(Iterable<Map.Entry<K, V>> iterable) {
		super(iterable);
	}

	public LabyrinthEntryMap(Iterable<Map.Entry<K, V>> iterable, int capacity) {
		super(iterable, capacity);
	}

	public ReplaceableKeyedValue<K, V> getFirst() {
		if (head == null) return null;
		return head.value;
	}

	public ReplaceableKeyedValue<K, V> getLast() {
		if (tail == null) return null;
		return tail.value;
	}

}
