package com.github.sanctum.labyrinth.data.container;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * This collection type is not linked! Entries can be both duplicated & out of natural order. This is merely a simple container.
 *
 * @param <K> The type of element this collection represents
 */
public final class LabyrinthArrayList<K> implements LabyrinthCollection<K> {

	private static final int INITIAL_CAPACITY = 10;
	private int size = 0;
	private Object[] data;


	public LabyrinthArrayList() {
		this(INITIAL_CAPACITY);
	}

	public LabyrinthArrayList(int capacity) {
		data = new Object[capacity];
	}

	public LabyrinthArrayList(Iterable<K> iterable) {
		this(INITIAL_CAPACITY);
		iterable.forEach(this::add);
	}

	public LabyrinthArrayList(Iterable<K> iterable, int capacity) {
		this(capacity);
		iterable.forEach(this::add);
	}

	@Override
	public K get(int index) throws IndexOutOfBoundsException {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException(index + " out of bounds for capacity " + size);
		}
		return (K) data[index];
	}

	@Override
	public boolean add(K k) {
		if (size == data.length) {
			int increased = data.length * 2;
			data = Arrays.copyOf(data, increased);
		}
		data[size++] = k;
		return true;
	}

	@Override
	public boolean addAll(Iterable<K> iterable) {
		boolean result = true;
		for (K k : iterable) {
			if (!contains(k)) {
				add(k);
			} else result = false;
		}
		return result;
	}

	@Override
	public boolean remove(K k) {
		int index = 0;
		 for (Object o : data) {
			 if (Objects.equals(o, k)) {
				 break;
			 }
			 index++;
		 }
		final int newSize;
		if ((newSize = size - 1) > index)
			System.arraycopy(data, index + 1, data, index, newSize - index);
		data[size = newSize] = null;
		return true;
	}

	@Override
	public boolean removeAll(Iterable<K> iterable) {
		boolean result = true;
		for (K k : iterable) {
			if (contains(k)) {
				remove(k);
			} else result = false;
		}
		return result;
	}

	@Override
	public boolean contains(K k) {
		for (Object o : data) {
			if (Objects.equals(o, k)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Iterable<K> iterable) {
		boolean result = true;
		for (K k : iterable) {
			if (!contains(k)) {
				result = false;
				break;
			}
		}
		return result;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		data = new Object[INITIAL_CAPACITY];
	}

	@NotNull
	@Override
	public Iterator<K> iterator() {
		return new Iterator<K>() {
			int index;

			@Override
			public boolean hasNext() {
				return index < data.length && data[index] != null;
			}

			@Override
			public K next() {
				final K element = (K) data[index];
				index++;
				return element;
			}
		};
	}

	public K[] toArray() {
		return (K[])data;
	}

	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			return (T[]) Arrays.copyOf(data, size, a.getClass());
		System.arraycopy(data, 0, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}

}
