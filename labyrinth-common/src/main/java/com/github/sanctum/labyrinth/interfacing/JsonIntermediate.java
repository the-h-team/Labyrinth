package com.github.sanctum.labyrinth.interfacing;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.data.JsonAdapter;
import com.github.sanctum.labyrinth.data.NodePointer;
import com.github.sanctum.labyrinth.data.service.AnnotationDiscovery;
import com.github.sanctum.labyrinth.data.service.Check;
import com.github.sanctum.labyrinth.data.service.DummyReducer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helpful utility class designed for easy json conversions
 * <p>
 * Specify an optional {@link NodePointer} in the instance of a singular object! It can be used
 * along side a {@link JsonAdapter}
 *
 * @author Hempfest
 */
public interface JsonIntermediate {

	/**
	 * Attempt to map all values within this class to a json object map.
	 *
	 * @return A new JsonObject
	 */
	default JsonObject toJsonObject() {
		return toJsonObject(this);
	}

	/**
	 * If this class represents that of iterable then attempt to map all values to a json object array.
	 *
	 * @return A new JsonArray
	 */
	default JsonArray toJsonArray() {
		if (this instanceof Iterable) {
			return toJsonArray((Iterable<?>) this);
		}
		return null;
	}

	/**
	 * Attempt to map all corresponding values from this class to a single json format.
	 *
	 * @return All contents as a json formatted string.
	 */
	default String toJsonString() {
		return toJsonString(this);
	}

	/**
	 * Attempt to map all linked values from the provided iterable to a json object array.
	 *
	 * @param collection The iterable to use.
	 * @return A new json array.
	 */
	static JsonArray toJsonArray(Iterable<?> collection) {
		JsonArray array = new JsonArray();
		for (Object o : Check.forNull(collection)) {
			if (o instanceof Number) {
				JsonElement element = new JsonPrimitive((Number) o);
				array.add(element);
			} else if (o instanceof Boolean) {
				JsonElement element = new JsonPrimitive((Boolean) o);
				array.add(element);
			} else if (o instanceof String) {
				JsonElement element = new JsonPrimitive((String) o);
				array.add(element);
			} else {
				JsonElement element = toJsonObject(o);
				array.add(element);
			}
		}
		return array;
	}

	/**
	 * Attempt to map all values from an object into a fresh json object.
	 *
	 * @param o The object to use.
	 * @return A new json object.
	 */
	static JsonObject toJsonObject(Object o) {
		JsonObject object = new JsonObject();
		if (o instanceof Map) {
			for (Map.Entry<String, Object> entry : ((Map<String, Object>) o).entrySet()) {
				if (entry.getValue() instanceof String) {
					object.addProperty(entry.getKey(), (String) entry.getValue());
				} else if (entry.getValue() instanceof Integer) {
					object.addProperty(entry.getKey(), (Integer) entry.getValue());
				} else if (entry.getValue() instanceof Float) {
					object.addProperty(entry.getKey(), (Float) entry.getValue());
				} else if (entry.getValue() instanceof Long) {
					object.addProperty(entry.getKey(), (Long) entry.getValue());
				} else if (entry.getValue() instanceof Double) {
					object.addProperty(entry.getKey(), (Double) entry.getValue());
				}
			}
			return object;
		}
		AnnotationDiscovery<Json, Object> discovery = AnnotationDiscovery.of(Json.class, Check.forNull(o)).filter(true);
		Map<Class<? extends Json.Reducer>, Json.Reducer> useable = new HashMap<>();
		if (discovery.isPresent()) {
			for (Method m : discovery) {
				try {
					Json annotation = m.getAnnotation(Json.class);
					Object invoke = m.invoke(o);
					if (invoke instanceof String) {
						object.addProperty(annotation.key(), (String) invoke);
					} else if (invoke instanceof Integer) {
						object.addProperty(annotation.key(), (Integer) invoke);
					} else if (invoke instanceof Float) {
						object.addProperty(annotation.key(), (Float) invoke);
					} else if (invoke instanceof Long) {
						object.addProperty(annotation.key(), (Long) invoke);
					} else if (invoke instanceof Double) {
						object.addProperty(annotation.key(), (Double) invoke);
					} else if (invoke instanceof Iterable) {
						object.add(annotation.key(), toJsonArray((Iterable<?>) invoke));
					} else {
						if (!annotation.reducer().equals(DummyReducer.class)) {
							Class<? extends Json.Reducer> clazz = annotation.reducer();
							if (useable.get(clazz) == null) {
								Constructor<? extends Json.Reducer> constructor = clazz.getDeclaredConstructor();
								constructor.setAccessible(true);
								Json.Reducer reducer = constructor.newInstance();
								useable.put(clazz, reducer);
								Object n = reducer.reduce(invoke);
								if (n instanceof String) {
									object.addProperty(annotation.key(), (String) n);
								} else if (n instanceof Integer) {
									object.addProperty(annotation.key(), (Integer) n);
								} else if (n instanceof Float) {
									object.addProperty(annotation.key(), (Float) n);
								} else if (n instanceof Long) {
									object.addProperty(annotation.key(), (Long) n);
								} else if (n instanceof Double) {
									object.addProperty(annotation.key(), (Double) n);
								} else {
									object.add(annotation.key(), toJsonObject(n));
								}
							} else {
								Json.Reducer reducer = useable.get(clazz);
								Object n = reducer.reduce(invoke);
								if (n instanceof String) {
									object.addProperty(annotation.key(), (String) n);
								} else if (n instanceof Integer) {
									object.addProperty(annotation.key(), (Integer) n);
								} else if (n instanceof Float) {
									object.addProperty(annotation.key(), (Float) n);
								} else if (n instanceof Long) {
									object.addProperty(annotation.key(), (Long) n);
								} else if (n instanceof Double) {
									object.addProperty(annotation.key(), (Double) n);
								} else {
									object.add(annotation.key(), toJsonObject(n));
								}
							}
						} else {
							object.add(annotation.key(), toJsonObject(invoke));
						}
					}

				} catch (Exception ignored) {
				}
			}
			String key = AnnotationDiscovery.of(NodePointer.class, o).mapFromClass((r, u) -> r.value());
			JsonObject parent = new JsonObject();
			if (key != null) {
				parent.add(key, object);
			} else {
				return object;
			}
			return parent;
		} else throw new NullPointerException("Class " + o.getClass() + " doesn't contain any json keys.");
	}

	/**
	 * Attempt to map all found values within the provided object to a single json format.
	 *
	 * @param o The object to use.
	 * @return A new json string.
	 */
	static String toJsonString(Object o) {
		if (o instanceof Iterable) {
			return toJsonArray((Iterable<?>)o).toString();
		} else
		return toJsonObject(o).toString();
	}

	/**
	 * Convert a json array to a list of objects, json objects convert to other maps and json arrays convert to other lists.
	 *
	 * @param array The json array to use.
	 * @return A list of objects.
	 */
	static List<Object> convertToList(JsonArray array) {
		List<Object> list = new ArrayList<>();
		for (JsonElement element : array) {
			if (element.isJsonObject()) {
				list.add(convertToMap(element.getAsJsonObject()));
			}
			if (element.isJsonArray()) {
				list.add(convertToList(element.getAsJsonArray()));
			}
			if (element.isJsonPrimitive()) {
				JsonPrimitive primitive = element.getAsJsonPrimitive();
				if (primitive.isBoolean()) {
					list.add(primitive.getAsBoolean());
				}
				if (primitive.isNumber()) {
					list.add(primitive.getAsNumber());
				}
				if (primitive.isString()) {
					list.add(primitive.getAsString());
				}
			}
		}
		return list;
	}

	/**
	 * Convert a json object into a map of keyed values, json objects convert to other maps and json arrays convert to other lists.
	 *
	 * @param object The json object to use.
	 * @return A map of keyed values.
	 */
	static Map<String, Object> convertToMap(JsonObject object) {
		Map<String, Object> map = new HashMap<>();
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			if (entry.getValue().isJsonObject()) {
				map.put(entry.getKey(), convertToMap(entry.getValue().getAsJsonObject()));
			}
			if (entry.getValue().isJsonArray()) {
				map.put(entry.getKey(), convertToList(entry.getValue().getAsJsonArray()));
			}
			if (entry.getValue().isJsonPrimitive()) {
				JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();
				if (primitive.isBoolean()) {
					map.put(entry.getKey(), primitive.getAsBoolean());
				}
				if (primitive.isNumber()) {
					map.put(entry.getKey(), primitive.getAsNumber());
				}
				if (primitive.isString()) {
					map.put(entry.getKey(), primitive.getAsString());
				}
			}
		}
		return map;
	}

}
