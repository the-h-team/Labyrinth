package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.data.ReplaceableKeyedValue;
import com.github.sanctum.labyrinth.library.ClassLookup;
import com.github.sanctum.labyrinth.library.HFEncoded;
import com.github.sanctum.labyrinth.library.TypeFlag;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import org.jetbrains.annotations.NotNull;

/**
 * The base abstraction for all factory labyrinth maps.
 *
 * @param <K> The key type for this map
 * @param <V> The value type for this map
 */
public abstract class LabyrinthSerializableMap<K extends Serializable, V extends Serializable> implements LabyrinthMap<K, V>, Serializable {

	private static final long serialVersionUID = -8231831281788627616L;
	protected SerializableEntryNode head, tail;
	protected int size;
	protected int capacity;
	protected final boolean capacityEnforced;

	public LabyrinthSerializableMap() {
		this.capacity = 10;
		this.capacityEnforced = false;
	}

	public LabyrinthSerializableMap(int capacity) {
		this.capacity = capacity;
		this.capacityEnforced = true;
	}

	public LabyrinthSerializableMap(Iterable<Map.Entry<K, V>> iterable) {
		this();
		iterable.forEach(entry -> put(entry.getKey(), entry.getValue()));
	}

	public LabyrinthSerializableMap(Iterable<Map.Entry<K, V>> iterable, int capacity) {
		this(capacity);
		iterable.forEach(entry -> put(entry.getKey(), entry.getValue()));
	}

	public static <M extends LabyrinthSerializableMap<K, V>, K extends Serializable, V extends Serializable> M deserialize(@NotNull Class<M> clazz, @NotNull String serialized, @NotNull ClassLoader classLoader) {
		return HFEncoded.of(serialized).deserialize(clazz, classLoader);
	}

	public static <M extends LabyrinthSerializableMap<K, V>, K extends Serializable, V extends Serializable> M deserialize(@NotNull Class<M> clazz, @NotNull String serialized, ClassLookup... lookups) {
		try {
			Object o = HFEncoded.of(serialized).deserialized(lookups);
			return clazz.cast(o);
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

	public static <M extends LabyrinthSerializableMap<K, V>, K extends Serializable, V extends Serializable> M deserialize(@NotNull String serialized, @NotNull ClassLoader classLoader) {
		TypeFlag<M> flag = TypeFlag.get();
		return HFEncoded.of(serialized).deserialize(flag.getType(), classLoader);
	}

	public static <M extends LabyrinthSerializableMap<K, V>, K extends Serializable, V extends Serializable> M deserialize(@NotNull String serialized, ClassLookup... lookups) {
		TypeFlag<M> flag = TypeFlag.get();
		try {
			Object o = HFEncoded.of(serialized).deserialized(lookups);
			return flag.cast(o);
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

	public static <K extends Serializable, V extends Serializable> LabyrinthSerializableMap<K, V> of(LabyrinthMap<K, V> assortment) {
		LabyrinthSerializableMap<K, V> map = new LabyrinthSerializableMap<K, V>(){};
		assortment.forEach(value -> map.put(value.getKey(), value.getValue()));
		return map;
	}

	public String serialize() {
		try {
			return HFEncoded.of(this).serialize();
		} catch (NotSerializableException e) {
			return null;
		}
	}

	protected class SerializableEntryNode implements Serializable {

		private static final long serialVersionUID = -8631304872537643808L;
		protected K k;
		protected V v;
		protected SerializableEntryNode next;


		SerializableEntryNode(SerializableEntryNode node) {
			this.k = node.k;
			this.v = node.v;
			this.next = node.next.copy();
		}

		SerializableEntryNode(K k, V v) {
			this.k = k;
			this.v = v;
			next = null;
		}

		SerializableEntryNode copy() {
			return new SerializableEntryNode(this);
		}

	}

	@Override
	public V put(K e, V value) {
		SerializableEntryNode imprint = getNode(e);
		if (imprint != null) {
			if (value == null) {
				remove(imprint);
			} else {
				imprint.v = value;
			}
			return value;
		}
		if (capacityEnforced) {
			if (size() >= capacity) return null;
		} else {
			if (size() >= capacity) capacity++;
		}
		SerializableEntryNode new_node = new SerializableEntryNode(e, value);
		if (head == null) {
			head = new_node;
		} else {
			SerializableEntryNode last = head;
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
		SerializableEntryNode current = head;
		while (current != null) {
			if (current.k == e || current.k.equals(e)) {
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
		SerializableEntryNode current = head;
		while (current != null) {
			if (current.k == key || current.k.equals(key)) {
				result = current.v;
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
		SerializableEntryNode current = head;
		while (current != null) {
			if (current.k == e || current.k.equals(e)) {
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
		SerializableEntryNode current = head;
		while (current != null) {
			if (current.v == v || current.v.equals(v)) {
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

			private SerializableEntryNode initial;

			{
				initial = head;
			}

			@Override
			public boolean hasNext() {
				return initial != null;
			}

			@Override
			public ReplaceableKeyedValue<K, V> next() {
				final ReplaceableKeyedValue<K, V> data = ReplaceableKeyedValue.of(initial.k, initial.v);
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

	SerializableEntryNode getNode(K key) {
		SerializableEntryNode result = null;
		SerializableEntryNode current = head;
		while (current != null) {
			if (current.k == key || current.k.equals(key)) {
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
				SerializableEntryNode previousToTail = head;
				while (previousToTail.next != tail)
					previousToTail = previousToTail.next;
				tail = previousToTail;
				tail.next = null;
			}
		}
		return true;
	}


	boolean remove(SerializableEntryNode node) {
		SerializableEntryNode currentNode = head;
		SerializableEntryNode prevNode = null;
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

}