package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.library.EasyTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
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
		try {
			if (file.exists()) {
				FileInputStream fileInputStream = new FileInputStream(file);
				InputStreamReader reader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
				json = (JSONObject) parser.parse(reader);
				reader.close();
				fileInputStream.close();
			} else {
				json = new JSONObject();
			}
		} catch (Exception ex) {
			json = new JSONObject();
		}
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
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
			json = (JSONObject) parser.parse(reader);
			reader.close();
			fileInputStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean save() {
		try {
			Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			Gson g = JsonAdapter.getJsonBuilder().setPrettyPrinting().disableHtmlEscaping().enableComplexMapKeySerialization().serializeNulls().serializeSpecialFloatingPointValues().create();
			g.toJson(json, Map.class, writer);
			writer.flush();
			writer.close();
			return true;
		} catch (Exception ex) {
			LabyrinthProvider.getService(Service.MESSENGER).getEmptyMailer().error("- An object of unknown origin was attempted to be saved and failed.").deploy();
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
		if (parent.exists()) {
			if (!file.exists()) {
				reload();
				return true;
			} else {
				return false;
			}
		}
		return parent.mkdirs() && file.createNewFile();
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
			Object os = ob.get(pathKey);
			if (os instanceof JSONObject) {
				ob = (JSONObject) os;
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
	Object checkObject(Type type, boolean array, Object object) {
		Object target = object;
		try {
			Class<?> cl = Class.forName(type.getTypeName());
			//if (type == ItemStack.class) type = JsonItemStack.class;
			if (target instanceof JSONObject) {
				JSONObject j = (JSONObject) object;
				Gson g = JsonAdapter.getJsonBuilder().create();

				Map.Entry<String, JsonAdapterInput<?>> d = serializers.entrySet().stream().filter(de -> de.getKey().equals(cl.getTypeName()) || cl.isAssignableFrom(de.getValue().getSubClass())).findFirst().orElse(null);
				if (d != null) {
					if (j.containsKey(d.getKey())) {
						Object ob = j.get(d.getKey());
						Object o;
						if (ob instanceof String) {
							Map<String, Object> map = g.fromJson((String) ob, new EasyTypeAdapter<Map<String, Object>>());
							o = d.getValue().read(map);
						} else {
							o = d.getValue().read((Map<String, Object>) ob);
						}
						if (o != null) {
							target = o;
						}
					} else {
						Object o = d.getValue().read(j);
						if (o != null) {
							target = o;
						}
					}
				}
				return target;
			}
			if (target instanceof JSONArray && array) {
				JSONArray j = (JSONArray) object;
				Map.Entry<String, JsonAdapterInput<?>> d = serializers.entrySet().stream().filter(de -> cl.isAssignableFrom(de.getValue().getSubClass())).findFirst().orElse(null);
				if (d != null) {
					Object[] copy = (Object[]) Array.newInstance(cl, j.size());
					for (int i = 0; i < j.size(); i++) {
						Map<String, Object> map = (Map<String, Object>) j.get(i);
						copy[i] = d.getValue().read(map.containsKey(d.getKey()) ? (Map<String, Object>) map.get(d.getKey()) : map);
					}
					target = copy;
				}
			}
		} catch (ClassNotFoundException exception) {
			LabyrinthProvider.getService(Service.MESSENGER).getEmptyMailer().error("- An issue occurred while attempting to deserialize object " + type.getTypeName()).deploy();
			exception.printStackTrace();
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
			} else {
				return obj;
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
					ob = checkObject(type, false, js.get(k));
					stop = true;
				} else {
					o = js;
				}
			} else {
				ob = checkObject(type.isArray() ? type.getComponentType() : type, obj instanceof JSONArray, obj);
				stop = true;
			}
		}
		if (!stop) {
			Object object = o.get(k);
			ob = checkObject(type.isArray() ? type.getComponentType() : type, (object instanceof JSONArray), object);
		}
		if (ob == null) return null;
		if (!type.isArray() && !type.isAssignableFrom(ob.getClass())) return null;
		return type.cast(ob);
	}

	@Override
	public Node getNode(String key) {
		return (Node) memory.entrySet().stream().filter(n -> n.getKey().equals(key)).map(Map.Entry::getValue).findFirst().orElseGet(() -> {
			ConfigurableNode n = new ConfigurableNode(key, this);
			memory.put(n.getPath(), n);
			return n;
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
		Object o = get(key);
		return String.valueOf(o);
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
	public String getPath() {
		String s = "/" + getName() + "/";
		if (getDirectory() != null) {
			s = s + getDirectory();
		}
		return s;
	}

	@Override
	public boolean isNode(String key) {
		String[] a = key.split("\\.");
		String k = a[Math.max(0, a.length - 1)];
		JSONObject o = json;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
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
		if (l.isEmpty()) return new ArrayList<>();
		if (!(l.get(0) instanceof String)) return new ArrayList<>();
		return (List<String>) l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getIntegerList(String key) {
		List<?> l = getList(key);
		if (l.isEmpty()) return new ArrayList<>();
		if (!(l.get(0) instanceof Integer) || !(l.get(0) instanceof Long)) return new ArrayList<>();
		return (List<Integer>) l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Double> getDoubleList(String key) {
		List<?> l = getList(key);
		if (l.isEmpty()) return new ArrayList<>();
		if (!(l.get(0) instanceof Double) || !(l.get(0) instanceof Float)) return new ArrayList<>();
		return (List<Double>) l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Float> getFloatList(String key) {
		List<?> l = getList(key);
		if (l.isEmpty()) return new ArrayList<>();
		if (!(l.get(0) instanceof Float) || !(l.get(0) instanceof Double)) return new ArrayList<>();
		return (List<Float>) l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getLongList(String key) {
		List<?> l = getList(key);
		if (l.isEmpty()) return new ArrayList<>();
		if (!(l.get(0) instanceof Long) || !(l.get(0) instanceof Integer)) return new ArrayList<>();
		return (List<Long>) l;
	}

	@Override
	public FileExtension getType() {
		return FileType.JSON;
	}
}
