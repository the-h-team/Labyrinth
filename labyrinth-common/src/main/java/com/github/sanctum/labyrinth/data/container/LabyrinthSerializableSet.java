package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.library.HFEncoded;
import com.github.sanctum.labyrinth.library.TypeFlag;
import java.io.NotSerializableException;
import java.io.Serializable;

public abstract class LabyrinthSerializableSet<E extends Serializable> extends LabyrinthCollectionBase<E> implements Serializable {

	private static final long serialVersionUID = -1413637500192757148L;

	public LabyrinthSerializableSet() {
		super();
	}

	public LabyrinthSerializableSet(int capacity) {
		super(capacity);
	}

	public LabyrinthSerializableSet(Iterable<E> iterable) {
		super(iterable);
	}

	public LabyrinthSerializableSet(Iterable<E> iterable, int capacity) {
		super(iterable, capacity);
	}

	public static <E extends Serializable> LabyrinthSerializableSet<E> deserialize(String serialized) {
		TypeFlag<LabyrinthSerializableSet<E>> flag = TypeFlag.get();
		return new HFEncoded(serialized).deserialize(flag.getType());
	}

	public static <E extends Serializable> LabyrinthSerializableSet<E> of(LabyrinthCollection<E> assortment) {
		LabyrinthSerializableSet<E> set = new LabyrinthSerializableSet<E>(){};
		set.addAll(assortment);
		return set;
	}

	public String serialize() {
		try {
			return new HFEncoded(this).serialize();
		} catch (NotSerializableException e) {
			return null;
		}
	}

}
