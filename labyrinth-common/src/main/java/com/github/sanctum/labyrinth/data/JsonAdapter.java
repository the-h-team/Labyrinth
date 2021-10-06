package com.github.sanctum.labyrinth.data;

import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import org.bukkit.Bukkit;

/**
 * An object capable of Json serialization & deserialization.
 * <p>
 * Annotated with {@link NodePointer} location information.
 *
 * @param <T> The object type this serializer represents.
 * @author Hempfest
 * @version 1.0
 */
public interface JsonAdapter<T> extends InstanceCreator<T> {

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

	@Override
	default T createInstance(Type type) {
		Class<?> c = TypeToken.get(type).getRawType();
		if (getClassType().isAssignableFrom(c)) {
			try {
				Object o = c.getDeclaredConstructor().newInstance();
				return (T) o;
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	static GsonBuilder getJsonBuilder() {
		GsonBuilder builder = new GsonBuilder();
		Configurable.serializers.forEach((key, value) -> builder.registerTypeHierarchyAdapter(value.getClassType(), value));
		return builder;
	}

}
