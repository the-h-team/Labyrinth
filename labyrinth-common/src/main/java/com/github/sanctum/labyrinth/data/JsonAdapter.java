package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.annotation.Comment;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

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

	@Comment(value = "Would like to be able to remove this in the future. Don't think it'll be possible.", author = "Hempfest")
	Class<T> getClassType();

	@Override
	@Note("Non bare constructors should have this method overridden, but it is not required.")
	default T createInstance(Type type) {
		Class<?> c = TypeToken.get(type).getRawType();
		if (getClassType().isAssignableFrom(c)) {
			try {
				return (T) c.getDeclaredConstructor().newInstance();
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

	static <T> LabyrinthCollection<T> read(@NotNull JsonAdapter<T> adapter, @NotNull List<Map<String, Object>> map) {
		LabyrinthCollection<T> collection = new LabyrinthList<>();
		map.forEach(m -> collection.add(adapter.read(m)));
		return collection;
	}

	static <T> JsonAdapter<T> get(Class<T> c) {
		return Configurable.getAdapter(c);
	}


}
