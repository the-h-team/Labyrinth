package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.library.TypeFlag;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * The base for all factory labyrinth collections.
 *
 * @param <E> The type of collection this is.
 */
public abstract class LabyrinthCollectionBase<E> implements LabyrinthCollection<E> {

	protected final boolean capacityEnforced;
	protected Node head, tail;
	protected int size;
	protected int capacity;

	public LabyrinthCollectionBase() {
		this.capacity = 10;
		this.capacityEnforced = false;
	}

	public LabyrinthCollectionBase(int capacity) {
		this.capacity = capacity;
		this.capacityEnforced = true;
	}

	public LabyrinthCollectionBase(Iterable<E> iterable) {
		this.capacity = 10;
		this.capacityEnforced = false;
		addAll(iterable);
	}

	public LabyrinthCollectionBase(Iterable<E> iterable, int capacity) {
		this(capacity);
		addAll(iterable);
	}

	@Override
	public boolean add(E e) {
		if (capacity > 0 && capacityEnforced) {
			if (size() >= capacity) return false;
		}
		Node new_node = new Node(e);
		if (head == null) {
			head = new_node;
		} else {
			Node last = head;
			while (last.next != null) {
				last = last.next;
			}
			last.next = new_node;
			tail = new_node;
		}
		size++;
		return true;
	}

	@Override
	public boolean addAll(Iterable<E> iterable) {
		boolean result = true;
		for (E e : iterable) {
			if (contains(e)) {
				result = false;
			} else {
				add(e);
			}
		}
		return result;
	}

	@Override
	public boolean remove(E e) {
		if (head == null) return false;
		Node current = head;
		while (current != null) {
			if (current.data == e || current.data.equals(e)) {
				size--;
				return remove(current);
			}
			current = current.next;
		}
		return false;
	}

	@Override
	public boolean removeAll(Iterable<E> iterable) {
		boolean result = true;
		for (E e : iterable) {
			if (!contains(e)) {
				result = false;
			} else {
				remove(e);
			}
		}
		return result;
	}

	@Override
	public E get(int index) {
		E result = null;
		Node current = head;
		int in = 0;
		while (current != null) {
			if (in == index) {
				result = current.data;
				break;
			}
			current = current.next;
			in++;
		}
		if (result == null)
			throw new IndexOutOfBoundsException("Index " + index + " out of bounds for capacity " + size());
		return result;
	}

	@Override
	public int size() {
		return size + 1;
	}

	public int capacity() {
		return capacity;
	}

	@Override
	public boolean contains(E e) {
		boolean found = false;
		Node current = head;
		while (current != null && !found) {
			if (current.data == e || current.data.equals(e)) {
				found = true;
			}
			current = current.next;
		}
		return found;
	}

	@Override
	public boolean containsAll(Iterable<E> iterable) {
		for (E e : iterable) {
			if (!contains(e)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void clear() {
		head = null;
		tail = null;
		size = -1;
	}

	public Object[] toArray() {
		TypeFlag<E> magic = TypeFlag.get();
		Object[] ar = (Object[]) Array.newInstance(magic.getType(), size());
		for (int i = 0; i < ar.length; i++) {
			ar[i] = get(i);
		}
		return ar;
	}

	public <R> R[] toArray(R[] r) {
		if (r.length < size)
			return (R[]) Arrays.copyOf(toArray(), size + 1, r.getClass());
		System.arraycopy(toArray(), 0, r, 0, size);
		if (r.length > size)
			r[size] = null;
		return r;
	}

	@NotNull
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			private Node initial;

			{
				initial = head;
			}

			@Override
			public boolean hasNext() {
				return initial != null;
			}

			@Override
			public E next() {
				final E data = initial.data;
				initial = initial.next;
				return data;
			}
		};
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		for (E e : this) {
			action.accept(e);
		}
	}

	@Override
	public Spliterator<E> spliterator() {
		return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
	}

	boolean removeFirst() {
		if (head == null) return false;
		if (head == tail) {
			head = null;
			tail = null;
		} else {
			head = head.next;
		}
		return true;
	}

	boolean removeLast() {
		if (tail == null) return false;
		if (head == tail) {
			head = null;
			tail = null;
		} else {

			Node previousToTail = head;

			while (previousToTail.next != tail)

				previousToTail = previousToTail.next;

			tail = previousToTail;

			tail.next = null;

		}
		return true;
	}

	boolean remove(Node node) {
		Node currentNode = head;
		Node prevNode = null;
		while (currentNode != null && !currentNode.equals(node)) {
			prevNode = currentNode;
			currentNode = currentNode.next;
		}
		if (currentNode == null) {
			return false;
		}
		if (prevNode == null) {
			return removeFirst();
		}
		if (prevNode.next.next != null) {
			// if there is, we follow the logic from the pseudo code
			prevNode.next = (prevNode.next.next);
		} else {
			return removeLast();
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder list = new StringBuilder();
		int count = 0;
		for (E e : this) {
			if (count == (size - 1)) {
				list.append(e.toString());
			} else {
				list.append(e.toString()).append(", ");
			}
			count++;
		}
		return "[" + list + "]";
	}

	protected class Node {

		protected E data;
		protected Node next;


		Node(Node node) {
			this.data = node.data;
			this.next = node.next.copy();
		}

		Node(E d) {
			data = d;
			next = null;
		}

		Node copy() {
			return new Node(this);
		}

	}

}