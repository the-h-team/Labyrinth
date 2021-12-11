package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.library.TypeFlag;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public abstract class LabyrinthSet<E> implements Iterable<E> {

	public static final int INITIAL_CAPACITY = -1;

	Node head, tail;
	int size = -1;
	final int capacity;

	public LabyrinthSet() {
		this(INITIAL_CAPACITY);
	}

	public LabyrinthSet(int capacity) {
		this.capacity = capacity;
	}

	public LabyrinthSet(Iterable<E> iterable) {
		this(INITIAL_CAPACITY);
		addAll(iterable);
	}

	public LabyrinthSet(Iterable<E> iterable, int capacity) {
		this(capacity);
		addAll(iterable);
	}

	public LabyrinthSet(LabyrinthSet<E> iterable) {
		this(iterable.capacity);
		this.head = iterable.head.copy();
		this.tail = iterable.tail.copy();
		this.size = iterable.size;
	}

	public LabyrinthSet(LabyrinthSet<E> iterable, int capacity) {
		this(capacity);
		this.head = iterable.head.copy();
		this.tail = iterable.tail.copy();
		this.size = iterable.size;
	}

	class Node {

		E data;
		Node next;
		final int index;


		Node(Node node) {
			this.data = node.data;
			this.next = node.next.copy();
			this.index = node.index;
		}

		Node(E d) {
			data = d;
			next = null;
			index = LabyrinthSet.this.size + 1;
		}

		Node copy() {
			return new Node(this);
		}

	}

	public boolean add(E e) {
		if (contains(e)) return false;
		if (capacity != -1) {
			if (size() >= capacity) return false;
		}
		Node new_node = new Node(e);
		new_node.next = null;
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

	public boolean addAll(Iterable<? extends E> iterable) {
		for (E e : iterable) {
			if (contains(e)) {
				return false;
			} else {
				add(e);
			}
		}
		return true;
	}

	public boolean remove(E e) {
		boolean removed = false;
		Node current = head;
		while (current != null && !removed) {
			if (current.next != null && current.next.data.equals(e)) {
				remove(current);
				removed = true;
				size--;
			}
			current = current.next;
		}
		return removed;
	}

	public boolean removeAll(Iterable<? extends E> iterable) {
		for (E e : iterable) {
			if (!contains(e)) {
				return false;
			} else {
				remove(e);
			}
		}
		return true;
	}

	public E get(int index) {
		E result = null;
		Node current = head;
		while (current != null) {
			if (current.index == index) {
				result = current.data;
				break;
			}
			current = current.next;
		}
		if (result == null) throw new IndexOutOfBoundsException("Index " + index + " out of bounds for " + size());
		return result;
	}

	public int size() {
		return size + 1;
	}

	public boolean contains(E e) {
		boolean found = false;
		Node current = head;
		while (current != null && !found) {
			if (current.data.equals(e)) {
				found = true;
			}
			current = current.next;
		}
		return found;
	}

	public boolean containsAll(Iterable<? extends E> iterable) {
		for (E e : iterable) {
			if (!contains(e)) {
				return false;
			}
		}
		return true;
	}

	public void clear() {
		head = null;
		tail = null;
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

	public Stream<E> stream() {
		Stream.Builder<E> builder = Stream.builder();
		forEach(builder::add);
		return builder.build();
	}

	boolean removeFirst() {

		if (head == null)

			return false;

		else {

			if (head == tail) {

				head = null;

				tail = null;

			} else {

				head = head.next;

			}

		}
		return true;
	}


	boolean removeLast() {

		if (tail == null)

			return false;

		else {

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

		}
		return true;
	}


	boolean remove(Node node) {

		if (node == null) {

			return removeFirst();

		} else if (node.next == tail) {

			tail = node;

			tail.next = null;

		} else if (node == tail) {

			return removeLast();

		} else {

			node.next = node.next.next;

		}
		return false;
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
		return Spliterators.spliterator(iterator(), size, 0);
	}

}