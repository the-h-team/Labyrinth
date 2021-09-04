package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.task.Schedule;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

/**
 * @author Hempfest
 * @version 1.0
 */
final class ConfigurableNode implements Node, Root, Primitive, Primitive.Bukkit {

	private final Configurable config;
	private final String key;

	protected ConfigurableNode(String key, Configurable configuration) {
		this.config = configuration;
		this.key = key;
	}

	@Override
	public boolean isNode(String key) {
		return config.isNode(this.key + "." + key);
	}

	@Override
	public Node getNode(String node) {
		return (Node) config.memory.entrySet().stream().filter(n -> n.getKey().equals(key + "." + node)).map(Map.Entry::getValue).findFirst().orElseGet(() -> {
			ConfigurableNode n = new ConfigurableNode(key + "." + node, config);
			config.memory.put(n.getPath(), n);
			return n;
		});
	}

	@Override
	public Object get() {
		return config.get(key);
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
	public String getString() {
		return config.getString(this.key);
	}

	@Override
	public int getInt() {
		return  config.getInt(this.key);
	}

	@Override
	public boolean getBoolean() {
		return config.getBoolean(this.key);
	}

	@Override
	public double getDouble() {
		return config.getDouble(this.key);
	}

	@Override
	public float getFloat() {
		return config.getFloat(this.key);
	}

	@Override
	public long getLong() {
		return config.getLong(this.key);
	}

	@Override
	public List<?> getList() {
		return config.getList(this.key);
	}

	@Override
	public Map<?, ?> getMap() {
		return config.getMap(this.key);
	}

	@Override
	public List<String> getStringList() {
		return config.getStringList(this.key);
	}

	@Override
	public List<Integer> getIntegerList() {
		return config.getIntegerList(this.key);
	}

	@Override
	public List<Double> getDoubleList() {
		return config.getDoubleList(this.key);
	}

	@Override
	public List<Float> getFloatList() {
		return config.getFloatList(this.key);
	}

	@Override
	public List<Long> getLongList() {
		return config.getLongList(this.key);
	}

	@Override
	public boolean isString() {
		return config.isString(this.key);
	}

	@Override
	public boolean isBoolean() {
		return config.isBoolean(this.key);
	}

	@Override
	public boolean isInt() {
		return config.isInt(this.key);
	}

	@Override
	public boolean isDouble() {
		return config.isDouble(this.key);
	}

	@Override
	public boolean isFloat() {
		return config.isFloat(this.key);
	}

	@Override
	public boolean isLong() {
		return config.isLong(this.key);
	}

	@Override
	public boolean isList() {
		return config.isList(this.key);
	}

	@Override
	public boolean isStringList() {
		return config.isStringList(this.key);
	}

	@Override
	public boolean isFloatList() {
		return config.isFloatList(this.key);
	}

	@Override
	public boolean isDoubleList() {
		return config.isDoubleList(this.key);
	}

	@Override
	public boolean isIntegerList() {
		return config.isIntegerList(this.key);
	}

	@Override
	public boolean isLongList() {
		return config.isLongList(this.key);
	}

	@Override
	public <T> T get(Class<T> type) {
		if (type.isAssignableFrom(ConfigurationSection.class)) {
			if (getRoot() instanceof YamlConfiguration) {
				YamlConfiguration conf = (YamlConfiguration) getRoot();
				return (T) conf.getConfig().getConfigurationSection(this.key);
			}
		}
		Object o = config.get(this.key, type);
		if (o != null) {
			return (T) o;
		}
		return null;
	}

	@Override
	public String getPath() {
		return this.key;
	}

	@Override
	public boolean delete() {
		if (config.isNode(this.key)) {
			config.set(this.key, null);
			Schedule.sync(() -> config.memory.remove(this)).run();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void reload() {
		config.reload();
	}

	@Override
	public boolean create() {
		if (!exists()) {
			set(new Object());
			save();
			return true;
		}
		return false;
	}

	@Override
	public boolean exists() {
		return isNode(this.key) || get() != null;
	}

	@Override
	public boolean save() {
		return config.save();
	}

	@Override
	public Configurable getRoot() {
		return config;
	}

	@Override
	public void set(Object o) {
		config.set(this.key, o);
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
		GsonBuilder builder = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().serializeNulls().setLenient().serializeSpecialFloatingPointValues();
		for (Map.Entry<String, JsonAdapterInput<?>> en : Configurable.serializers.entrySet()) {
			builder.registerTypeAdapter(en.getValue().getType(), en.getValue());
		}
		return builder.create().toJson(get());
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		if (getRoot() instanceof YamlConfiguration) {
			return get(ConfigurationSection.class).getKeys(deep);
		} else {
			Set<String> keys = new HashSet<>();
			JsonConfiguration json = (JsonConfiguration) getRoot();
			for (Object o : json.json.entrySet()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) o;
				if (deep) {
					if (entry.getValue() instanceof JSONObject) {
						JSONObject obj = (JSONObject) entry.getValue();
						for (Object ob : obj.entrySet()) {
							Map.Entry<String, Object> en = (Map.Entry<String, Object>) ob;
							if (en.getValue() instanceof JSONObject) {
								JSONObject j = (JSONObject) entry.getValue();
								for (Object e : j.entrySet()) {
									Map.Entry<String, Object> ent = (Map.Entry<String, Object>) e;
									if (ent.getValue() instanceof JSONObject) {
										JSONObject ja = (JSONObject) ent.getValue();
										for (Object ex : ja.entrySet()) {
											Map.Entry<String, Object> entr = (Map.Entry<String, Object>) ex;
											keys.add(key + "." + entry.getKey() + "." + en.getKey() + "." + ent.getKey() + "." + entr.getKey());
										}
									} else {
										keys.add(key + "." + entry.getKey() + "." + en.getKey() + "." + ent.getKey());
									}
								}
							} else {
								keys.add(key + "." + entry.getKey() + "." + en.getKey());
							}
						}
					} else {
						keys.add(key + "." + entry.getKey());
					}
				} else {
					keys.add(key + "." + entry.getKey());
				}
			}
			return keys;
		}
	}

	@Override
	public Map<String, Object> getValues(boolean deep) {
		if (getRoot() instanceof YamlConfiguration) {
			return get(ConfigurationSection.class).getValues(deep);
		} else {
			Map<String, Object> map = new HashMap<>();
			JsonConfiguration json = (JsonConfiguration) getRoot();
			for (Object o : json.json.entrySet()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) o;
				if (deep) {
					if (entry.getValue() instanceof JSONObject) {
						JSONObject obj = (JSONObject) entry.getValue();
						for (Object ob : obj.entrySet()) {
							Map.Entry<String, Object> en = (Map.Entry<String, Object>) ob;
							if (en.getValue() instanceof JSONObject) {
								JSONObject j = (JSONObject) entry.getValue();
								for (Object e : j.entrySet()) {
									Map.Entry<String, Object> ent = (Map.Entry<String, Object>) e;
									if (ent.getValue() instanceof JSONObject) {
										JSONObject ja = (JSONObject) ent.getValue();
										for (Object ex : ja.entrySet()) {
											Map.Entry<String, Object> entr = (Map.Entry<String, Object>) ex;
											map.put(key + "." + entry.getKey() + "." + en.getKey() + "." + ent.getKey() + "." + entr.getKey(), entr.getValue());
										}
									} else {
										map.put(key + "." + entry.getKey() + "." + en.getKey() + "." + ent.getKey(), ent.getValue());
									}
								}
							} else {
								map.put(key + "." + entry.getKey() + "." + en.getKey(), en.getValue());
							}
						}
					} else {
						map.put(key + "." + entry.getKey(), entry.getValue());
					}
				} else {
					 map.put(key + "." + entry.getKey(), entry.getValue());
				}
			}
			return map;
		}
	}

	@Override
	public boolean isLocation() {
		return config.isLocation(this.key);
	}

	@Override
	public boolean isItemStack() {
		return config.isItemStack(this.key);
	}

	@Override
	public Location getLocation() {
		return config.getLocation(this.key);
	}

	@Override
	public ItemStack getItemStack() {
		return config.getItemStack(this.key);
	}
}
