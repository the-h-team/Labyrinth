package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.data.ReplaceableKeyedValue;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import org.jetbrains.annotations.NotNull;

/**
 * The base abstraction for all factory labyrinth maps.
 *
 * @param <K> The key type for this map
 * @param <V> The value type for this map
 */
public abstract class LabyrinthMapBase<K, V> implements LabyrinthMap<K, V> {

	public static final int INITIAL_CAPACITY = -1;

	protected Node head, tail;
	protected int size;
	protected final int capacity;

	public LabyrinthMapBase() {
		this(INITIAL_CAPACITY);
	}

	public LabyrinthMapBase(int capacity) {
		this.capacity = capacity;
	}

	public LabyrinthMapBase(Iterable<Map.Entry<K, V>> iterable) {
		this(INITIAL_CAPACITY);
		iterable.forEach(entry -> put(entry.getKey(), entry.getValue()));
	}

	public LabyrinthMapBase(Iterable<Map.Entry<K, V>> iterable, int capacity) {
		this(capacity);
		iterable.forEach(entry -> put(entry.getKey(), entry.getValue()));
	}

	protected class Node {

		protected ReplaceableKeyedValue<K, V> value;
		protected Node next;


		Node(Node node) {
			this.value = node.value;
			this.next = node.next.copy();
		}

		Node(K k, V v) {
			this.value = ReplaceableKeyedValue.of(k, v);
			next = null;
		}

		Node(ImmutableKeyedValue<K, V> value) {
			this.value = value;
			next = null;
		}

		Node copy() {
			return new Node(this);
		}

	}

	@Override
	public V put(K e, V value) {
		Node imprint = getNode(e);
		if (imprint != null) {
			if (value == null) {
				remove(imprint);
			} else {
				imprint.value.setValue(value);
			}
			return value;
		}
		if (capacity != -1) {
			if (size() >= capacity) return null;
		}
		Node new_node = new Node(e, value);
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
		return value;
	}

	public boolean putAll(Iterable<Map.Entry<K, V>> iterable) {
		boolean result = true;
		for (Map.Entry<K, V> entry : iterable) {
			if (containsKey(entry.getKey())) {
				result = false;
			} else put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	@Override
	public boolean remove(K e) {
		Node current = head;
		while (current != null) {
			if (current.value.getKey() == e || current.value.getKey().equals(e)) {
				size--;
				return remove(current);
			}
			current = current.next;
		}
		return false;
	}

	public boolean removeAll(Iterable<Map.Entry<K, V>> iterable) {
		boolean result = true;
		for (Map.Entry<K, V> entry : iterable) {
			if (!containsKey(entry.getKey())) {
				result = false;
			} else remove(entry.getKey());
		}
		return result;
	}

	@Override
	public V get(K key) {
		V result = null;
		Node current = head;
		while (current != null) {
			if (current.value.getKey() == key || current.value.getKey().equals(key)) {
				result = current.value.getValue();
				break;
			}
			current = current.next;
		}
		return result;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean containsKey(K e) {
		boolean found = false;
		Node current = head;
		while (current != null) {
			if (current.value.getKey() == e || current.value.getKey().equals(e)) {
				found = true;
				break;
			}
			current = current.next;
		}
		return found;
	}

	@Override
	public boolean containsValue(V v) {
		boolean found = false;
		Node current = head;
		while (current != null) {
			if (current.value.getValue() == v || current.value.getValue().equals(v)) {
				found = true;
				break;
			}
			current = current.next;
		}
		return found;
	}

	@Override
	public void clear() {
		head = null;
		tail = null;
		size = 0;
	}

	@Override
	public Spliterator<ReplaceableKeyedValue<K, V>> spliterator() {
		return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
	}

	@NotNull
	@Override
	public Iterator<ReplaceableKeyedValue<K, V>> iterator() {
		return new Iterator<ReplaceableKeyedValue<K, V>>() {

			private Node initial;

			{
				initial = head;
			}

			@Override
			public boolean hasNext() {
				return initial != null;
			}

			@Override
			public ReplaceableKeyedValue<K, V> next() {
				final ReplaceableKeyedValue<K, V> data = initial.value;
				initial = initial.next;
				return data;
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder list = new StringBuilder();
		int count = 0;
		for (ReplaceableKeyedValue<K, V> e : this) {
			if (count == (size - 1)) {
				list.append(e.toString());
			} else {
				list.append(e.toString()).append(", ");
			}
			count++;
		}
		return "[" + list + "]";
	}

	Node getNode(K key) {
		Node result = null;
		Node current = head;
		while (current != null) {
			if (current.value.getKey() == key || current.value.getKey().equals(key)) {
				result = current;
				break;
			}
			current = current.next;
		}
		return result;
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

	static class ImmutableKeyedValue<K, V> implements ReplaceableKeyedValue<K, V> {

		private final K k;
		private final V v;

		ImmutableKeyedValue(K k, V v) {
			this.k = k;
			this.v = v;
		}

		@Override
		public V setValue(V value) {
			throw warning();
		}

		@Override
		public @NotNull K getKey() {
			return k;
		}

		@Override
		public V getValue() {
			return v;
		}

		static RuntimeException warning() {
			return new ImmutableStorageException("Element modifications cannot be made to immutable maps!");
		}

		@Override
		public String toString() {
			return "Entry{key=" + k + ", value=" + v + "}";
		}
	}
}