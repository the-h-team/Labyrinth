package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.data.service.AnnotationDiscovery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * An internal adapter for Json serialization context.
 *
 * @param <T> The type for this adapter.
 * @author Hempfest
 * @version 1.0
 */
abstract class JsonAdapterInput<T> implements JsonAdapter<T>, JsonSerializer<T>, JsonDeserializer<T> {

	protected final JsonAdapter<T> serializer;

	private final Gson gson = new GsonBuilder().create();

	protected JsonAdapterInput(JsonAdapter<T> serializer) {
		this.serializer = serializer;
	}

	@Override
	public JsonElement serialize(T t, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonObject o = new JsonObject();
		if (getKey() == null)
			throw new RuntimeException("Serializable data for JSON files must be annotated with NodePointer");
		o.add(getKey(), write(t));
		return o;
	}

	@Override
	public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		Map<String, Object> map = gson.fromJson(jsonElement, new TypeToken<Map<String, Object>>(){}.getType());
		return read(map);
	}

	public String getKey() {
		String test = AnnotationDiscovery.of(NodePointer.class, serializer).mapFromClass((r, u) -> r.value());
		return test != null ? test : AnnotationDiscovery.of(NodePointer.class, serializer.getClassType()).mapFromClass((r, u) -> r.value());
	}

	@Override
	public JsonElement write(T t) {
		return serializer.write(t);
	}

	@Override
	public T read(Map<String, Object> object) {
		return serializer.read(object);
	}

	@Override
	public Class<T> getClassType() {
		return serializer.getClassType();
	}

	static final class Impl<T> extends JsonAdapterInput<T> {

		protected Impl(JsonAdapter<T> serializer) {
			super(serializer);
		}
	}

}
