package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.annotation.See;
import com.github.sanctum.labyrinth.data.ReplaceableKeyedValue;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A custom map type, this class will retain each element fed into it in the exact order it was received.
 * <p>
 * The practices of this map type explicitly follows o(1) time complexity when inserting new tail elements,
 * when removing or retrieving an element o(n) time complexity takes place.
 *
 * @param <K> The key type of element this collection is for.
 * @param <V> The value type of element this collection is for.
 */
@See({LabyrinthEntryMap.class, LabyrinthAtlasMap.class})
public interface LabyrinthMap<K, V> extends Iterable<ReplaceableKeyedValue<K, V>> {

	/**
	 * Get a value from this map by its key.
	 *
	 * @param k The keyed value to retrieve.
	 * @return The existing value or null.
	 */
	V get(K k);

	/**
	 * Put a keyed value into this map, if a value already exists under the specified key it will be overwritten.
	 *
	 * @param k The key to use.
	 * @param v the value to link to this key.
	 * @return The freshly submitted value.
	 */
	V put(K k, V v);

	/**
	 * Insert a map entry iterable into this map.
	 *
	 * @param iterable The iterable entries to consume.
	 * @return true if all elements were added.
	 */
	boolean putAll(Iterable<Map.Entry<K, V>> iterable);

	/**
	 * Remove a value from this map by its designated key.
	 *
	 * @param k The key to query
	 * @return true if the specified keyed value was removed.
	 */
	boolean remove(K k);

	/**
	 * Remove each entry from the iterable from this map.
	 *
	 * @param iterable The iterable entries to remove
	 * @return true if every element was removed.
	 */
	boolean removeAll(Iterable<Map.Entry<K, V>> iterable);

	/**
	 * Check if a specified key is mapped.
	 *
	 * @param k The key to check existence of
	 * @return true if the specified key exists within this map.
	 */
	boolean containsKey(K k);

	/**
	 * Check if a specified value is mapped.
	 *
	 * @param v The value to check existent of
	 * @return true if the specified value exists within this map.
	 */
	boolean containsValue(V v);

	/**
	 * @return the size of this map.
	 */
	int size();

	/**
	 * Clear all keyed values from this map.
	 */
	void clear();

	/**
	 * Check if this map is empty.
	 *
	 * @return true if this map is empty.
	 */
	default boolean isEmpty() {
		return size() <= 0;
	}

	/**
	 * Get a value from this map by its key.
	 * <p>
	 * If the specified key isn't existent map a new value and retrieve that.
	 *
	 * @param key   The key to query.
	 * @param value The value for the specified key.
	 * @return an existing value or a newly mapped one.
	 * @see this#put(Object, Object)
	 */
	default V computeIfAbsent(K key, V value) {
		V test = get(key);
		return test != null ? test : put(key, value);
	}

	/**
	 * Get a value from this map by its key.
	 * <p>
	 * If the specified key isn't existent map a new value and retrieve that.
	 *
	 * @param key   The key to query.
	 * @param value The value for the specified key.
	 * @return an existing value or a newly mapped one.
	 * @see this#put(Object, Object)
	 */
	default V computeIfAbsent(K key, Supplier<V> value) {
		V test = get(key);
		return test != null ? test : put(key, value.get());
	}

	/**
	 * Get a value from this map by its key.
	 * <p>
	 * If the specified key isn't existent map a new value and retrieve that.
	 *
	 * @param key      The key to query.
	 * @param function The value consumer for the specified key.
	 * @return an existing value or a newly mapped one.
	 * @see this#put(Object, Object)
	 */
	default V computeIfAbsent(K key, Function<K, V> function) {
		V test = get(key);
		return test != null ? test : put(key, function.apply(key));
	}

	/**
	 * Get all entries from within this map.
	 *
	 * @return a collection of replaceable keyed values.
	 */
	default LabyrinthCollection<ReplaceableKeyedValue<K, V>> entries() {
		LabyrinthCollectionBase<ReplaceableKeyedValue<K, V>> keys = new LabyrinthSet<>();
		forEach(keys::add);
		return keys;
	}

	/**
	 * Get all keys from within this map.
	 *
	 * @return a collection of mapped keys.
	 */
	default LabyrinthCollection<K> keys() {
		LabyrinthSet<K> keys = new LabyrinthSet<>();
		forEach(entry -> keys.add(entry.getKey()));
		return ImmutableLabyrinthCollection.of(keys);
	}

	/**
	 * Get all values from within this map.
	 *
	 * @return a collection of mapped values.
	 */
	default LabyrinthCollection<V> values() {
		LabyrinthList<V> keys = new LabyrinthList<>();
		forEach(entry -> keys.add(entry.getValue()));
		return keys;
	}

	/**
	 * @return a fresh stream containing all entries from this map.
	 */
	@See(LabyrinthCollectors.class)
	default Stream<ReplaceableKeyedValue<K, V>> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

}
