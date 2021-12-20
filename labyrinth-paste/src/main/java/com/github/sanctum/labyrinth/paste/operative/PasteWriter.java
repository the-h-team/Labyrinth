package com.github.sanctum.labyrinth.paste.operative;

import com.github.sanctum.labyrinth.data.JsonAdapter;
import com.github.sanctum.labyrinth.interfacing.JsonIntermediate;
import com.github.sanctum.labyrinth.library.HFEncoded;
import java.io.NotSerializableException;
import java.util.Collection;

/**
 * Signifies an object that can write information to the web using provided data
 */
public interface PasteWriter {

	/**
	 * Write information to a web connection.
	 *
	 * @param info the information to write.
	 * @return a response from the web.
	 */
	PasteResponse write(String... info);

	/**
	 * Write information to a web connection.
	 *
	 * @param info the information to write.
	 * @return a response from the web.
	 */
	PasteResponse write(Collection<? extends CharSequence> info);

	/**
	 * Write information to a web connection using serializable data.
	 *
	 * @param t the data to write
	 * @param serialize whether to serialize the data for you using {@link HFEncoded}
	 * @param <T> the data type.
	 * @return a response from the web.
	 * @throws NotSerializableException if attempting serialization and the data isn't serializable.
	 */
	default <T> PasteResponse write(T t, boolean serialize) throws NotSerializableException {
		if (serialize) {
			String serialized = HFEncoded.of(t).serialize();
			return write(serialized);
		}
		if (t instanceof JsonIntermediate) {
			return write(((JsonIntermediate)t).toJsonString());
		}
		if (t instanceof JsonAdapter) {
			return write(((JsonAdapter)t).write(t).toString());
		}
		return write(t.toString());
	}

}
