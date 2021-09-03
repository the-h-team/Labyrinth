package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.formatting.string.RandomHex;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author Hempfest
 * @version 1.0
 */
public class JsonConfiguration extends Configurable {

	protected final Plugin plugin;
	private final File file;
	private final File parent;
	private final String name;
	private final String directory;
	protected JSONObject json;
	private final JSONParser parser;

	protected JsonConfiguration(Plugin plugin, String fileType, String name, String directory) {
		this.parser = new JSONParser();
		this.name = name;
		this.plugin = plugin;
		this.directory = directory;
		final File pluginDataDir = plugin.getDataFolder();
		if (!pluginDataDir.exists()) {
			//noinspection ResultOfMethodCallIgnored
			pluginDataDir.mkdir();
		}

		final File parent = (directory == null || directory.isEmpty()) ? pluginDataDir : new File(pluginDataDir, directory);
		if (!parent.exists()) {
			//noinspection ResultOfMethodCallIgnored
			parent.mkdir();
		}
		this.parent = parent;
		if (fileType == null) fileType = "data";
		this.file = new File(parent, name.concat("." + fileType));
		reload();
	}

	@Override
	public void reload() {
		try {
			if (!file.exists()) {
				PrintWriter writer = new PrintWriter(file, "UTF-8");
				writer.print("{");
				writer.print("}");
				writer.flush();
				writer.close();
			}
			json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean save() {
		try {
			Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			GsonBuilder gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().enableComplexMapKeySerialization().serializeNulls().serializeSpecialFloatingPointValues().setLenient();
			for (JsonAdapterContext<?> serializer : serializers.values()) {
				gson.registerTypeAdapter(serializer.getType(), serializer);
			}
			Gson g = gson.create();
			g.toJson(json, Map.class, writer);
			writer.flush();
			writer.close();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete() {
		return file.delete();
	}

	@Override
	public boolean create() throws IOException {
		return parent.exists() ? file.createNewFile() : parent.mkdir() && file.createNewFile();
	}

	@Override
	public boolean exists() {
		return parent.exists() && file.exists();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDirectory() {
		return this.directory;
	}

	@Override
	public File getParent() {
		return file;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void set(String key, Object o) {
		String[] a = key.split("\\.");
		String k = a[Math.max(0, a.length - 1)];
		JSONObject ob = json;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			JSONObject j = (JSONObject) ob.get(pathKey);
			if (j != null) {
				ob = j;
			} else {
				JSONObject n = new JSONObject();
				ob.put(pathKey, n);
				ob = (JSONObject) ob.get(pathKey);
			}
		}
		if (o == null) {
			ob.remove(k);
			return;
		}
		if (o instanceof Map) {
			ob.put(k, new JSONObject((Map<?, ?>) o));
			return;
		}
		if (o instanceof Collection) {
			JSONArray ar = new JSONArray();
			ar.addAll((List<?>) o);
			ob.put(k, ar);
			return;
		}
		ob.put(k, o);
	}

	@SuppressWarnings("unchecked")
	private Object checkObject(Type type, Object object) {
		Object target = object;
		//if (type == ItemStack.class) type = JsonItemStack.class;
		if (type == CustomColor.class) type = RandomHex.class;
		if (target instanceof JSONObject) {
			JSONObject j = (JSONObject) object;
			Gson g = new GsonBuilder().create();
			Type finalType = type;
			Map.Entry<String, JsonAdapterContext<?>> d = serializers.entrySet().stream().filter(de -> de.getValue().getType().getTypeName().equals(finalType.getTypeName())).findFirst().orElse(null);
			if (d != null) {
				if (j.containsKey(d.getKey())) {
					Object ob = j.get(d.getKey());
					Object o;
					if (ob instanceof String) {
						Map<String, Object> map = g.fromJson((String) ob, new TypeToken<Map<String, Object>>() {
						}.getType());
						o = d.getValue().read(map);
					} else {
						o = d.getValue().read((Map<String, Object>) ob);
					}
					if (o != null) {
						target = o;
					}
				}
			}
		}
		return target;
	}

	@Override
	protected Object get(String key) {
		String[] a = key.split("\\.");
		String k = a[Math.max(0, a.length - 1)];
		JSONObject o = json;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			Object obj = o.get(pathKey);
			if (obj instanceof JSONObject) {
				JSONObject js = (JSONObject) obj;
				if (js.containsKey(k)) {
					return js.get(k);
				} else {
					o = js;
				}
			}
		}
		return o.get(k);
	}

	@Override
	protected <T> T get(String key, Class<T> type) {
		boolean stop = false;
		Object ob = null;
		String[] a = key.split("\\.");
		String k = a[Math.max(0, a.length - 1)];
		JSONObject o = json;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			Object obj = o.get(pathKey);
			if (obj instanceof JSONObject) {
				JSONObject js = (JSONObject) obj;
				if (js.containsKey(k)) {
					ob = checkObject(type, js.get(k));
					stop = true;
				} else {
					o = js;
				}
			}
		}
		if (!stop) {
			ob = checkObject(type, o.get(k));
		}
		if (ob == null) return null;
		if (!type.isAssignableFrom(ob.getClass())) return null;
		return (T) ob;
	}

	@Override
	public Node getNode(String key) {
		return nodes.stream().filter(n -> n.getName().equals(key)).findFirst().orElseGet(() -> {
			ConfigurableNode node = new ConfigurableNode(key, this);
			nodes.add(node);
			return node;
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getKeys(boolean deep) {
		Set<String> keys = new HashSet<>();
		for (Object o : json.entrySet()) {
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
										keys.add(entry.getKey() + "." + en.getKey() + "." + ent.getKey() + "." + entr.getKey());
									}
								} else {
									keys.add(entry.getKey() + "." + en.getKey() + "." + ent.getKey());
								}
							}
						} else {
							keys.add(entry.getKey() + "." + en.getKey());
						}
					}
				} else {
					keys.add(entry.getKey());
				}
			} else {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getValues(boolean deep) {
		Map<String, Object> map = new HashMap<>();
		for (Object o : json.entrySet()) {
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
										map.put(entry.getKey() + "." + en.getKey() + "." + ent.getKey() + "." + entr.getKey(), entr.getValue());
									}
								} else {
									map.put(entry.getKey() + "." + en.getKey() + "." + ent.getKey(), ent.getValue());
								}
							}
						} else {
							map.put(entry.getKey() + "." + en.getKey(), en.getValue());
						}
					}
				} else {
					map.put(entry.getKey(), entry.getValue());
				}
			} else {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return map;
	}

	@Override
	public Location getLocation(String key) {
		return get(key, Location.class);
	}

	@Override
	public ItemStack getItemStack(String key) {
		return get(key, ItemStack.class);
	}

	@Override
	public String getString(String key) {
		return get(key).toString();
	}

	@Override
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(getString(key));
	}

	@Override
	public boolean isLocation(String key) {
		return getLocation(key) != null;
	}

	@Override
	public boolean isList(String key) {
		return get(key) instanceof List;
	}

	@Override
	public boolean isStringList(String key) {
		return !getStringList(key).isEmpty();
	}

	@Override
	public boolean isFloatList(String key) {
		return !getFloatList(key).isEmpty();
	}

	@Override
	public boolean isDoubleList(String key) {
		return !getDoubleList(key).isEmpty();
	}

	@Override
	public boolean isLongList(String key) {
		return !getLongList(key).isEmpty();
	}

	@Override
	public boolean isIntegerList(String key) {
		return !getIntegerList(key).isEmpty();
	}

	@Override
	public boolean isItemStack(String key) {
		return getItemStack(key) != null;
	}

	@Override
	public boolean isBoolean(String key) {
		return get(key) instanceof Boolean;
	}

	@Override
	public boolean isDouble(String key) {
		return get(key) instanceof Double;
	}

	@Override
	public boolean isInt(String key) {
		return get(key) instanceof Integer;
	}

	@Override
	public boolean isLong(String key) {
		return get(key) instanceof Long;
	}

	@Override
	public boolean isFloat(String key) {
		return get(key) instanceof Float;
	}

	@Override
	public boolean isString(String key) {
		return get(key) instanceof String;
	}

	@Override
	public boolean isNode(String key) {
		String[] a = key.split("\\.");
		String k = a[Math.max(0, a.length - 1)];
		JSONObject o = json;
		for (String pathKey : a) {
			Object obj = o.get(pathKey);
			if (obj instanceof JSONObject) {
				JSONObject js = (JSONObject) obj;
				if (js.containsKey(k)) {
					return js.get(k) instanceof JSONObject;
				} else {
					o = js;
				}
			}
		}
		return o.get(k) instanceof JSONObject;
	}

	@Override
	public double getDouble(String key) {
		try {
			return Double.parseDouble(getString(key));
		} catch (Exception ignored) {
		}
		return 0.0;
	}

	@Override
	public long getLong(String key) {
		try {
			return Long.parseLong(getString(key));
		} catch (Exception ignored) {
		}
		return 0L;
	}

	@Override
	public float getFloat(String key) {
		try {
			return Float.parseFloat(getString(key));
		} catch (Exception ignored) {
		}
		return 0.0f;
	}

	@Override
	public int getInt(String key) {
		try {
			return Integer.parseInt(getString(key));
		} catch (Exception ignored) {
		}
		return 0;
	}

	@Override
	public Map<?, ?> getMap(String key) {
		Object o = get(key);
		if (o instanceof Map) {
			return (Map<?, ?>) o;
		}
		return new HashMap<>();
	}

	@Override
	public List<?> getList(String key) {
		Object o = get(key);
		if (o instanceof List) {
			return (List<?>) o;
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getStringList(String key) {
		List<?> l = getList(key);
		if (!(l.get(0) instanceof String)) return new ArrayList<>();
		return (List<String>) l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getIntegerList(String key) {
		List<?> l = getList(key);
		if (!(l.get(0) instanceof Integer) || !(l.get(0) instanceof Long)) return new ArrayList<>();
		return (List<Integer>) l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Double> getDoubleList(String key) {
		List<?> l = getList(key);
		if (!(l.get(0) instanceof Double) || !(l.get(0) instanceof Float)) return new ArrayList<>();
		return (List<Double>) l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Float> getFloatList(String key) {
		List<?> l = getList(key);
		if (!(l.get(0) instanceof Float) || !(l.get(0) instanceof Double)) return new ArrayList<>();
		return (List<Float>) l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getLongList(String key) {
		List<?> l = getList(key);
		if (!(l.get(0) instanceof Long)) return new ArrayList<>();
		return (List<Long>) l;
	}

	@Override
	public FileType getType() {
		return FileType.JSON;
	}
}
