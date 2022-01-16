package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.data.ReplaceableKeyedValue;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

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
		Node storage = new Node(new ImmutableKeyedValue<>(k, v));
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
	@Deprecated
	public V put(K e, V value) {
		throw ImmutableKeyedValue.warning();
	}

	@Override
	@Deprecated
	public boolean putAll(Iterable<Map.Entry<K, V>> iterable) {
		throw ImmutableKeyedValue.warning();
	}

	@Override
	@Deprecated
	public boolean removeAll(Iterable<Map.Entry<K, V>> iterable) {
		throw ImmutableKeyedValue.warning();
	}

	@Override
	@Deprecated
	public boolean remove(K e) {
		throw ImmutableKeyedValue.warning();
	}

	@Override
	@Deprecated
	public void clear() {
		throw ImmutableKeyedValue.warning();
	}

	@Override
	@Deprecated
	public V computeIfAbsent(K key, V value) {
		throw ImmutableKeyedValue.warning();
	}

	@Override
	@Deprecated
	public V computeIfAbsent(K key, Function<K, V> function) {
		throw ImmutableKeyedValue.warning();
	}

	public static <K, V> ImmutableLabyrinthMap<K, V> of(LabyrinthMap<K, V> map) {
		return new ImmutableLabyrinthMap<K, V>(map){};
	}

	public static <K, V> ImmutableLabyrinthMap<K, V> of(Map<K, V> map) {
		return new ImmutableLabyrinthMap<K, V>(map){};
	}

	public static <K, V> Builder<K, V> builder() {
		return new Builder<>();
	}

	public static final class Builder<K, V> {

		private final LabyrinthMap<K, V> internal;

		Builder() {
			internal = new LabyrinthEntryMap<>();
		}

		public Builder<K, V> put(K key, V value) {
			internal.put(key, value);
			return this;
		}

		public Builder<K, V> computeIfAbsent(K k, V v) {
			internal.computeIfAbsent(k, v);
			return this;
		}

		public Builder<K, V> computeIfAbsent(K k, Supplier<V> v) {
			internal.computeIfAbsent(k, v);
			return this;
		}

		public Builder<K, V> computeIfAbsent(K k, Function<K, V> v) {
			internal.computeIfAbsent(k, v);
			return this;
		}

		public ImmutableLabyrinthMap<K, V> build() {
			return of(internal);
		}

	}


}
