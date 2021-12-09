package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.library.TypeFlag;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Map;

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

	@Deprecated
	Class<T> getClassType();

	/**
	 * @return The class this serializer represents in relation to T.
	 */
	@Note("To be overridden!")
	default Class<? extends T> getSubClass() {
		return getClassType();
	}

	@Override
	@Note("Non bare constructors should have this method overridden!")
	default T createInstance(Type type) {
		Class<?> c = TypeToken.get(type).getRawType();
		if (getSubClass().isAssignableFrom(c)) {
			try {
				return getSubClass().cast(c.getDeclaredConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	static GsonBuilder getJsonBuilder() {
		GsonBuilder builder = new GsonBuilder();
		Configurable.serializers.forEach((key, value) -> builder.registerTypeHierarchyAdapter(value.getSubClass(), value));
		return builder;
	}

	/**
	 * @see Configurable#registerClass(Class)
	 */
	static void register(Class<? extends JsonAdapter<?>> adapterClass) {
		Configurable.registerClass(adapterClass);
	}

	/**
	 * @see Configurable#registerClass(Class, Object...)
	 */
	static void register(Class<? extends JsonAdapter<?>> adapterClass, Object... args) {
		Configurable.registerClass(adapterClass, args);
	}


}
