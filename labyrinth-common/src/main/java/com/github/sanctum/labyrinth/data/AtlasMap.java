package com.github.sanctum.labyrinth.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AtlasMap implements Atlas {

	protected final Map<String, Object> SOURCE = new HashMap<>();
	protected final Map<String, MemorySpace> QUERY = new HashMap<>();
	protected final char divider;

	public AtlasMap(char divider) {
		this.divider = divider;
	}

	public AtlasMap() {
		this.divider = '.';
	}

	String dividerAdapt() {
		return divider == '.' ? "[" + divider + "]" : divider + "";
	}

	@Override
	public boolean isNode(String key) {
		String[] a = key.split(dividerAdapt());
		String k = a[Math.max(0, a.length - 1)];
		Map<String, Object> o = SOURCE;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			Object obj = o.get(pathKey);
			if (obj instanceof Map) {
				Map<String, Object> js = (Map<String, Object>) obj;
				if (js.containsKey(k)) {
					return js.get(k) instanceof Map;
				} else {
					o = js;
				}
			}
		}
		return o.get(k) instanceof Map;
	}

	@Override
	public Node getNode(String key) {
		return (Node) QUERY.entrySet().stream().filter(e -> e.getKey().equals(key)).map(Entry::getValue).findFirst().orElseGet(() -> {
			AtlasNode n = new AtlasNode(key, this);
			QUERY.put(key, n);
			return n;
		});
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		Set<String> keys = new HashSet<>();
		SOURCE.forEach((key, value) -> {
			if (deep) {
				if (value instanceof Map) {
					keys.addAll(MapDecompressionUtils.getInstance().decompress(((Map<String, Object>) value).entrySet(), divider, null).toSet());
				} else {
					keys.add(key);
				}
			} else {
				keys.add(key);
			}
		});
		return keys;
	}

	@Override
	public Map<String, Object> getValues(boolean deep) {
		Map<String, Object> map = new HashMap<>();
		SOURCE.forEach((key, value) -> {
			if (deep) {
				if (value instanceof Map) {
					map.putAll(MapDecompressionUtils.getInstance().decompress(((Map<String, Object>) value).entrySet(), divider, null).toMap());
				} else {
					map.put(key, value);
				}
			} else {
				map.put(key, value);
			}
		});
		return map;
	}

	@Override
	public int size() {
		return getValues(true).size();
	}

	@Override
	public boolean isEmpty() {
		return SOURCE.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public Object get(Object key) {
		String ke = (String) key;
		String[] a = ke.split(dividerAdapt());
		String k = a[Math.max(0, a.length - 1)];
		Map<String, Object> o = SOURCE;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			Object obj = o.get(pathKey);
			if (obj instanceof Map) {
				Map<String, Object> js = (Map<String, Object>) obj;
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

	@Nullable
	@Override
	public Object put(String key, Object o) {
		String[] a = key.split(dividerAdapt());
		String k = a[Math.max(0, a.length - 1)];
		Map<String, Object> ob = SOURCE;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			Object os = ob.get(pathKey);
			if (os instanceof Map) {
				ob = (Map<String, Object>) os;
			} else {
				Map<String, Object> n = new HashMap<>();
				ob.put(pathKey, n);
				ob = (Map<String, Object>) ob.get(pathKey);
			}
		}
		if (o == null) {
			ob.remove(k);
			QUERY.remove(k);
			return null;
		}
		ob.put(k, o);
		return o;
	}

	@Override
	public Object remove(Object key) {
		return put((String) key, null);
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ?> m) {
		SOURCE.putAll(m);
	}

	@Override
	public void clear() {
		SOURCE.clear();
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		return getKeys(true);
	}

	@NotNull
	@Override
	public Collection<Object> values() {
		return getValues(true).values();
	}

	@NotNull
	@Override
	public Set<Entry<String, Object>> entrySet() {
		return getValues(true).entrySet();
	}

	@Override
	public Object getOrDefault(Object key, Object defaultValue) {
		Object o = get(key);
		if (o == null) {
			return defaultValue;
		}
		return o;
	}

	@Override
	public void forEach(BiConsumer<? super String, ? super Object> action) {
		getValues(true).forEach(action);
	}

	@Override
	public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
		getValues(true).replaceAll(function);
	}

	@Nullable
	@Override
	public Object putIfAbsent(String key, Object value) {
		Object o = get(key);
		if (o == null) {
			return put(key, value);
		}
		return o;
	}

	@Override
	public boolean remove(Object key, Object value) {
		Object o = get(key);
		if (o == null) return false;
		if (Objects.equals(o, value)) {
			put((String) key, null);
			return true;
		}
		return false;
	}

	@Override
	public boolean replace(String key, Object oldValue, Object newValue) {
		Object o = get(key);
		if (o == null) return false;
		if (Objects.equals(o, oldValue)) {
			put(key, newValue);
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public Object replace(String key, Object value) {
		Object o = get(key);
		if (o == null) return null;
		put(key, value);
		return value;
	}

	@Override
	public Object computeIfAbsent(String key, @NotNull Function<? super String, ?> mappingFunction) {
		Object t = get(key);
		if (t == null) {
			return put(key, mappingFunction.apply(key));
		}
		return t;
	}

	@Override
	public Object computeIfPresent(String key, @NotNull BiFunction<? super String, ? super Object, ?> remappingFunction) {
		Object t = get(key);
		if (t != null) {
			return put(key, remappingFunction.apply(key, t));
		}
		return null;
	}

	@Override
	public Object compute(String key, @NotNull BiFunction<? super String, ? super Object, ?> remappingFunction) {
		Object t = get(key);
		return put(key, remappingFunction.apply(key, t));
	}

	@Override
	public Object merge(String key, @NotNull Object value, @NotNull BiFunction<? super Object, ? super Object, ?> remappingFunction) {
		return SOURCE.merge(key, value, remappingFunction);
	}

	@Override
	public char getDivider() {
		return divider;
	}
}
