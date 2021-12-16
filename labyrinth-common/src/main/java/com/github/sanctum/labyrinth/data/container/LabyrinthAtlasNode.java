package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.data.Node;
import com.github.sanctum.labyrinth.data.Primitive;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class LabyrinthAtlasNode implements Node, Primitive, Primitive.Bukkit {

	private final LabyrinthAtlasMap MAP;
	private final String key;

	public LabyrinthAtlasNode(@NotNull String key, @NotNull LabyrinthAtlasMap parent) {
		this.key = key;
		this.MAP = parent;
	}

	@Override
	public Object get() {
		return MAP.get(this.key);
	}

	@Override
	public Primitive toPrimitive() {
		return this;
	}

	@Override
	public Bukkit toBukkit() {
		return this;
	}

	@Override
	public <T> T get(Class<T> type) {
		Object o = get();
		if (o == null) return null;
		if (!type.isAssignableFrom(o.getClass())) return null;
		return (T) o;
	}

	@Override
	public void set(Object o) {
		MAP.put(this.key, o);
	}

	@Override
	public Node getParent() {
		String[] k = this.key.split("//.");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < k.length - 1; i++) {
			builder.append(k[i]).append(".");
		}
		String key = builder.toString();
		if (key.endsWith(".")) {
			key = key.substring(0, builder.length() - 1);
		}
		if (key.equals(this.key)) return this;
		return getNode(key);
	}

	@Override
	public String toJson() {
		Gson g = new GsonBuilder().create();
		return g.toJson(get());
	}

	@Override
	public String getPath() {
		return this.key;
	}

	@Override
	public boolean isNode(String node) {
		return MAP.isNode(this.key + "." + node);
	}

	@Override
	public Node getNode(String node) {
		return (Node) Optional.ofNullable(MAP.QUERY.get(this.key + "." + node)).orElseGet(() -> {
			LabyrinthAtlasNode n = new LabyrinthAtlasNode(this.key + "." + node, MAP);
			MAP.QUERY.put(this.key + "." + node, n);
			return n;
		});
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		Set<String> keys = new HashSet<>();
		if (get() instanceof Map) {
			Map<String, Object> map1 = (Map<String, Object>) get();
			if (deep) {
				for (Map.Entry<String, Object> entry : map1.entrySet()) {
					if (entry.getValue() instanceof Map) {
						Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
						for (Map.Entry<String, Object> entry2 : map2.entrySet()) {
							if (entry2.getValue() instanceof Map) {
								Map<String, Object> map3 = (Map<String, Object>) entry2.getValue();
								for (Map.Entry<String, Object> entry3 : map3.entrySet()) {
									if (entry3.getValue() instanceof Map) {
										Map<String, Object> map4 = (Map<String, Object>) entry2.getValue();
										for (Map.Entry<String, Object> entry4 : map4.entrySet()) {
											keys.add(this.key + "." + entry.getKey() + "." + entry2.getKey() + "." + entry3.getKey() + "." + entry4.getKey());
										}
									} else {
										keys.add(entry.getKey() + "." + entry2.getKey() + "." + entry3.getKey());
									}
								}
							} else {
								keys.add(entry.getKey() + "." + entry2.getKey());
							}
						}
					}
					keys.add(entry.getKey());
				}
			} else {
				for (Map.Entry<String, Object> entry : map1.entrySet()) {
					keys.add(entry.getKey());
				}
			}
		} else {
			keys.add(this.key);
		}
		return keys;
	}

	@Override
	public Map<String, Object> getValues(boolean deep) {
		Map<String, Object> map = new HashMap<>();
		if (get() instanceof Map) {
			Map<String, Object> map1 = (Map<String, Object>) get();
			if (deep) {
				for (Map.Entry<String, Object> entry : map1.entrySet()) {
					if (entry.getValue() instanceof Map) {
						Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
						for (Map.Entry<String, Object> entry2 : map2.entrySet()) {
							if (entry2.getValue() instanceof Map) {
								Map<String, Object> map3 = (Map<String, Object>) entry2.getValue();
								for (Map.Entry<String, Object> entry3 : map3.entrySet()) {
									if (entry3.getValue() instanceof Map) {
										Map<String, Object> map4 = (Map<String, Object>) entry2.getValue();
										for (Map.Entry<String, Object> entry4 : map4.entrySet()) {
											map.put(this.key + "." + entry.getKey() + "." + entry2.getKey() + "." + entry3.getKey() + "." + entry4.getKey(), entry4.getValue());
										}
									} else {
										map.put(entry.getKey() + "." + entry2.getKey() + "." + entry3.getKey(), entry3.getValue());
									}
								}
							} else {
								map.put(entry.getKey() + "." + entry2.getKey(), entry2.getValue());
							}
						}
					}
					map.put(entry.getKey(), entry.getValue());
				}
			} else {
				for (Map.Entry<String, Object> entry : map1.entrySet()) {
					if (entry.getValue() instanceof Map) {
						Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
						for (Map.Entry<String, Object> entry2 : map2.entrySet()) {
							if (entry2.getValue() instanceof Map) {
								Map<String, Object> map3 = (Map<String, Object>) entry2.getValue();
								for (Map.Entry<String, Object> entry3 : map3.entrySet()) {
									if (entry3.getValue() instanceof Map) {
										Map<String, Object> map4 = (Map<String, Object>) entry2.getValue();
										for (Map.Entry<String, Object> entry4 : map4.entrySet()) {
											map.put(this.key + "." + entry.getKey() + "." + entry2.getKey() + "." + entry3.getKey() + "." + entry4.getKey(), entry4.getValue());
										}
									} else {
										map.put(entry.getKey() + "." + entry2.getKey() + "." + entry3.getKey(), entry3.getValue());
									}
								}
							} else {
								map.put(entry.getKey() + "." + entry2.getKey(), entry2.getValue());
							}
						}
					} else {
						map.put(entry.getKey(), entry.getValue());
					}
				}
			}
		} else {
			map.put(this.key, get());
		}
		return map;
	}

	@Override
	public boolean save() {
		return false;
	}

	@Override
	public boolean delete() {
		MAP.put(this.key, null);
		return true;
	}

	@Override
	public void reload() {

	}

	@Override
	public boolean create() {
		return false;
	}

	@Override
	public boolean exists() {
		return MAP.getKeys(true).contains(this.key);
	}

	@Override
	public String getString() {
		Object o = get();
		if (o == null) return null;
		if (!(o instanceof String)) return null;
		return (String) o;
	}

	@Override
	public int getInt() {
		Object o = get();
		if (o == null) return 0;
		if (!(o instanceof Integer)) return 0;
		return (int) o;
	}

	@Override
	public boolean getBoolean() {
		Object o = get();
		if (o == null) return false;
		if (!(o instanceof Boolean)) return false;
		return (boolean) o;
	}

	@Override
	public double getDouble() {
		Object o = get();
		if (o == null) return 0.0;
		if (!(o instanceof Double) && !(o instanceof Integer)) return 0.0;
		return (double) o;
	}

	@Override
	public float getFloat() {
		Object o = get();
		if (o == null) return 0.0f;
		if (!(o instanceof Double) && !(o instanceof Float)) return 0.0f;
		return (float) o;
	}

	@Override
	public long getLong() {
		Object o = get();
		if (o == null) return 0;
		if (!(o instanceof Long) && !(o instanceof Integer)) return 0;
		return (long) o;
	}

	@Override
	public List<?> getList() {
		Object o = get();
		if (o == null) return null;
		if (!(o instanceof List)) return null;
		return (List<?>) o;
	}

	@Override
	public Map<?, ?> getMap() {
		Object o = get();
		if (o == null) return null;
		if (!(o instanceof Map)) return null;
		return (HashMap<?, ?>) o;
	}

	@Override
	public List<String> getStringList() {
		Object o = get();
		if (o == null) return null;
		if (!(o instanceof List)) return null;
		if (!(((List<?>) o)).isEmpty() && !(((List<?>) o).get(0) instanceof String)) return null;
		return (List<String>) o;
	}

	@Override
	public List<Integer> getIntegerList() {
		Object o = get();
		if (o == null) return null;
		if (!(o instanceof List)) return null;
		if (!(((List<?>) o)).isEmpty() && !(((List<?>) o).get(0) instanceof Integer)) return null;
		return (List<Integer>) o;
	}

	@Override
	public List<Double> getDoubleList() {
		Object o = get();
		if (o == null) return null;
		if (!(o instanceof List)) return null;
		if (!(((List<?>) o)).isEmpty() && !(((List<?>) o).get(0) instanceof Double)) return null;
		return (List<Double>) o;
	}

	@Override
	public List<Float> getFloatList() {
		Object o = get();
		if (o == null) return null;
		if (!(o instanceof List)) return null;
		if (!(((List<?>) o)).isEmpty() && !(((List<?>) o).get(0) instanceof Float)) return null;
		return (List<Float>) o;
	}

	@Override
	public List<Long> getLongList() {
		Object o = get();
		if (o == null) return null;
		if (!(o instanceof List)) return null;
		if (!(((List<?>) o)).isEmpty() && !(((List<?>) o).get(0) instanceof Long)) return null;
		return (List<Long>) o;
	}

	@Override
	public boolean isString() {
		return getString() != null;
	}

	@Override
	public boolean isBoolean() {
		return get() instanceof Boolean;
	}

	@Override
	public boolean isInt() {
		return get() instanceof Integer;
	}

	@Override
	public boolean isDouble() {
		return get() instanceof Double;
	}

	@Override
	public boolean isFloat() {
		return get() instanceof Float;
	}

	@Override
	public boolean isLong() {
		return get() instanceof Long;
	}

	@Override
	public boolean isList() {
		return get() instanceof List;
	}

	@Override
	public boolean isStringList() {
		return getStringList() != null && !getStringList().isEmpty();
	}

	@Override
	public boolean isFloatList() {
		return getFloatList() != null && !getFloatList().isEmpty();
	}

	@Override
	public boolean isDoubleList() {
		return getDoubleList() != null && !getDoubleList().isEmpty();
	}

	@Override
	public boolean isIntegerList() {
		return getIntegerList() != null && !getIntegerList().isEmpty();
	}

	@Override
	public boolean isLongList() {
		return getLongList() != null && !getLongList().isEmpty();
	}

	@Override
	public boolean isLocation() {
		return get() instanceof Location;
	}

	@Override
	public boolean isItemStack() {
		return get() instanceof ItemStack;
	}

	@Override
	public Location getLocation() {
		Object o = get();
		if (o == null) return null;
		if (!Location.class.isAssignableFrom(o.getClass())) return null;
		return (Location) o;
	}

	@Override
	public ItemStack getItemStack() {
		Object o = get();
		if (o == null) return null;
		if (!ItemStack.class.isAssignableFrom(o.getClass())) return null;
		return (ItemStack) o;
	}
}
