package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.library.HFEncoded;
import com.github.sanctum.labyrinth.library.TypeFlag;
import java.io.NotSerializableException;
import java.io.Serializable;

public final class SerializableLabyrinthSet<E extends Serializable> extends LabyrinthSet<E> implements Serializable {

	private static final long serialVersionUID = -1413637500192757148L;

	public static <E extends Serializable> SerializableLabyrinthSet<E> deserialize(String serialized) {
		TypeFlag<SerializableLabyrinthSet<E>> flag = TypeFlag.get();
		return new HFEncoded(serialized).deserialize(flag.getType());
	}

	public String serialize() {
		try {
			return new HFEncoded(this).serialize();
		} catch (NotSerializableException e) {
			return null;
		}
	}

}
