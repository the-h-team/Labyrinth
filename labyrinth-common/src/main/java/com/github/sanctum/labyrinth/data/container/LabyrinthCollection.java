package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.annotation.See;
import java.util.stream.Stream;

/**
 * A custom collection type, this class will retain each element fed into it in the exact order it was received.
 *
 * The practices of this collection type explicitly follows o(1) time complexity when inserting new tail elements,
 * when removing or retrieving an element o(n) time complexity takes place.
 *
 * @param <K> The type of element this collection is for.
 */
@See({LabyrinthList.class, LabyrinthSet.class, LabyrinthSerializableSet.class})
public interface LabyrinthCollection<K> extends Iterable<K> {

	/**
	 * Get an element from this collection at a specific index.
	 *
	 * @throws IndexOutOfBoundsException if the specified index goes beyond the natural scope.
	 * @param index the index of the value to retrieve.
	 * @return The value or null.
	 */
	K get(int index) throws IndexOutOfBoundsException;

	/**
	 * Add a new element to the tail end of this collection.
	 *
	 * @param k The element to add.
	 * @return true if the element was added false if something went wrong.
	 */
	boolean add(K k);

	/**
	 * Add an iterable of relative source type to this collection.
	 *
	 * @param iterable the iterable to consume.
	 * @return true if all elements from the iterable were successfully added.
	 */
	boolean addAll(Iterable<K> iterable);

	/**
	 * Remove an element contained within this collection.
	 *
	 * @param k The element to be removed.
	 * @return true if the element was successfully removed.
	 */
	boolean remove(K k);

	/**
	 * Remove elements from this collection matching contents from the relative iterable.
	 *
	 * @param iterable the iterable to remove elements from
	 * @return true if all elements from the iterable were removed from this collection.
	 */
	boolean removeAll(Iterable<K> iterable);

	/**
	 * Check if this collection contains a specific element.
	 *
	 * @param k The element to check existence for.
	 * @return true if this collection contains the specified element.
	 */
	boolean contains(K k);

	/**
	 * Check if this collection contains all elements provided from the iterable.
	 *
	 * @param iterable The iterable to query.
	 * @return true if this collection contains the iterated elements.
	 */
	boolean containsAll(Iterable<K> iterable);

	/**
	 * @return the size of this collection.
	 */
	int size();

	/**
	 * Clear all retained elements from this collection.
	 */
	void clear();

	/**
	 * Check if this collection is empty.
	 *
	 * @return true if this collection is empty.
	 */
	default boolean isEmpty() {
		return size() <= 0;
	}

	/**
	 * @return A new element stream containing all the elements from this collection.
	 */
	@See(LabyrinthCollectors.class)
	default Stream<K> stream() {
		Stream.Builder<K> builder = Stream.builder();
		forEach(builder::add);
		return builder.build();
	}

}
