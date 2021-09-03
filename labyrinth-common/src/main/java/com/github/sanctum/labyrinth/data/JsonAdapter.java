package com.github.sanctum.labyrinth.data;

import com.google.gson.JsonElement;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * An object capable of Json serialization & deserialization.
 *
 * @param <T> The object type this serializer represents.
 * @author Hempfest
 * @version 1.0
 */
public interface JsonAdapter<T> {

	/**
	 * Serialize the corresponding element for json.
	 *
	 * @param t The object to serialize.
	 * @return The serialized json object.
	 */
	JsonElement write(T t);

	/**
	 * Deserialize the corresponding value from its map into a fresh instance.
	 *
	 * @param object The map of information to read from.
	 * @return The deserialized object.
	 */
	T read(Map<String, Object> object);

	/**
	 * @return The class this serializer represents.
	 */
	Class<T> getClassType();

	/**
	 * @return The type this serializer represents.
	 */
	default Type getType() {
		return getClassType();
	}

}
