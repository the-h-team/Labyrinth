package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.library.ClassLookup;
import com.github.sanctum.labyrinth.library.HFEncoded;
import com.github.sanctum.labyrinth.library.TypeFlag;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public abstract class LabyrinthSerializableSet<E extends Serializable> implements LabyrinthCollection<E>, Serializable {

	private static final long serialVersionUID = -1413637500192757148L;

	protected final boolean capacityEnforced;
	protected SerializableNode head, tail;
	protected int size;
	protected int capacity;

	public LabyrinthSerializableSet() {
		this.capacity = 10;
		this.capacityEnforced = false;
	}

	public LabyrinthSerializableSet(int capacity) {
		this.capacity = capacity;
		this.capacityEnforced = true;
	}

	public LabyrinthSerializableSet(Iterable<E> iterable) {
		this.capacity = 10;
		this.capacityEnforced = false;
		addAll(iterable);
	}

	public LabyrinthSerializableSet(Iterable<E> iterable, int capacity) {
		this(capacity);
		addAll(iterable);
	}

	public static <L extends LabyrinthSerializableSet<E>, E extends Serializable> L deserialize(@NotNull Class<L> clazz, @NotNull String serialized, @NotNull ClassLoader classLoader) {
		return HFEncoded.of(serialized).deserialize(clazz, classLoader);
	}

	public static <L extends LabyrinthSerializableSet<E>, E extends Serializable> L deserialize(@NotNull Class<L> clazz, @NotNull String serialized, ClassLookup... lookups) {
		try {
			Object o = HFEncoded.of(serialized).deserialized(lookups);
			return clazz.cast(o);
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

	public static <E extends Serializable> LabyrinthSerializableSet<E> deserialize(@NotNull String serialized, @NotNull ClassLoader classLoader) {
		TypeFlag<LabyrinthSerializableSet<E>> flag = TypeFlag.get();
		return HFEncoded.of(serialized).deserialize(flag.getType(), classLoader);
	}

	public static <E extends Serializable> LabyrinthSerializableSet<E> deserialize(@NotNull String serialized, ClassLookup... lookups) {
		TypeFlag<LabyrinthSerializableSet<E>> flag = TypeFlag.get();
		try {
			Object o = HFEncoded.of(serialized).deserialized(lookups);
			return flag.cast(o);
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

	public static <E extends Serializable> LabyrinthSerializableSet<E> of(LabyrinthCollection<E> assortment) {
		LabyrinthSerializableSet<E> set = new LabyrinthSerializableSet<E>(){};
		set.addAll(assortment);
		return set;
	}

	public String serialize() {
		try {
			return HFEncoded.of(this).serialize();
		} catch (NotSerializableException e) {
			return null;
		}
	}

	@Override
	public boolean add(E e) {
		if (capacity > 0 && capacityEnforced) {
			if (size() >= capacity) return false;
		}
		SerializableNode new_node = new SerializableNode(e);
		if (head == null) {
			head = new_node;
		} else {
			SerializableNode last = head;
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
		SerializableNode current = head;
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
		SerializableNode current = head;
		int in = 0;
		while (current != null) {
			if (in == index) {
				result = current.data;
				break;
			}
			current = current.next;
			in++;
		}
		if (index >= size || index < 0)
			throw new IndexOutOfBoundsException("Index " + index + " out of bounds for capacity " + size());
		return result;
	}

	@Override
	public int size() {
		return size;
	}

	public int capacity() {
		return capacity;
	}

	@Override
	public boolean contains(E e) {
		boolean found = false;
		SerializableNode current = head;
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
			return (R[]) Arrays.copyOf(toArray(), size, r.getClass());
		System.arraycopy(toArray(), 0, r, 0, size);
		if (r.length > size)
			r[size] = null;
		return r;
	}

	@NotNull
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			private SerializableNode initial;

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

			SerializableNode previousToTail = head;

			while (previousToTail.next != tail)

				previousToTail = previousToTail.next;

			tail = previousToTail;

			tail.next = null;

		}
		return true;
	}

	boolean remove(SerializableNode node) {
		SerializableNode currentNode = head;
		SerializableNode prevNode = null;
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

	protected class SerializableNode implements Serializable {

		private static final long serialVersionUID = -5322657138191786239L;
		protected E data;
		protected SerializableNode next;


		SerializableNode(SerializableNode node) {
			this.data = node.data;
			this.next = node.next.copy();
		}

		SerializableNode(E d) {
			data = d;
			next = null;
		}

		SerializableNode copy() {
			return new SerializableNode(this);
		}

	}

}
