package com.github.sanctum.labyrinth.data.container;

import java.util.Map;
import java.util.function.Function;

/**
 * Represents a {@link LabyrinthMap} that cannot be modified only read from.
 *
 * @param <K> The type of map this is.
 */
public abstract class ImmutableLabyrinthMap<K, V> extends LabyrinthMapBase<K, V> {

	ImmutableLabyrinthMap(LabyrinthMap<K, V> map) {
		map.forEach(entry -> addImmutable(entry.getKey(), entry.getValue()));
	}

	ImmutableLabyrinthMap(Map<K, V> map) {
		map.forEach(this::addImmutable);
	}

	void addImmutable(K k, V v) {
		Node storage = new Node(new IrreplaceableKeyedValue<>(k, v));
		storage.next = null;
		if (head == null) {
			head = storage;
		} else {
			Node last = head;
			while (last.next != null) {
				last = last.next;
			}
			last.next = storage;
		}
		tail = storage;
		size++;
	}

	@Override
	public V put(K e, V value) {
		throw IrreplaceableKeyedValue.warning();
	}

	@Override
	public boolean remove(K e) {
		throw IrreplaceableKeyedValue.warning();
	}

	@Override
	public void clear() {
		throw IrreplaceableKeyedValue.warning();
	}

	@Override
	public V computeIfAbsent(K key, V value) {
		throw IrreplaceableKeyedValue.warning();
	}

	@Override
	public V computeIfAbsent(K key, Function<K, V> function) {
		throw IrreplaceableKeyedValue.warning();
	}

	public static <K, V> ImmutableLabyrinthMap<K, V> of(LabyrinthMap<K, V> map) {
		return new ImmutableLabyrinthMap<K, V>(map){};
	}

	public static <K, V> ImmutableLabyrinthMap<K, V> of(Map<K, V> map) {
		return new ImmutableLabyrinthMap<K, V>(map){};
	}


}
