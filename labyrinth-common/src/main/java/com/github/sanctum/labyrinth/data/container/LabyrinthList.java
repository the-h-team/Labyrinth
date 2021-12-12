package com.github.sanctum.labyrinth.data.container;

/**
 * A list of elements in linked order, allowing for duplicate entries.
 *
 * @see LabyrinthCollection
 * @param <E> The type of list this is
 */
public final class LabyrinthList<E> extends LabyrinthCollectionBase<E> {

	public LabyrinthList() {
		super();
	}

	public LabyrinthList(int capacity) {
		super(capacity);
	}

	public LabyrinthList(Iterable<E> iterable) {
		super(iterable);
	}

	public LabyrinthList(Iterable<E> iterable, int capacity) {
		super(iterable, capacity);
	}

	/**
	 * @return the first element in this list or null.
	 */
	public E getFirst() {
		if (head == null) return null;
		return head.data;
	}

	/**
	 * @return the last element in this list, could be the same as the first element or null.
	 */
	public E getLast() {
		if (tail == null) return null;
		return tail.data;
	}
}
