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

	private final Map<String, Object> SOURCE = new HashMap<>();
	protected final Map<String, MemorySpace> QUERY = new HashMap<>();

	@Override
	public boolean isNode(String key) {
		String[] a = key.split("\\.");
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
		for (Object o : SOURCE.entrySet()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) o;
			if (deep) {
				if (entry.getValue() instanceof Map) {
					Map<String, Object> obj = (Map<String, Object>) entry.getValue();
					for (Object ob : obj.entrySet()) {
						Map.Entry<String, Object> en = (Map.Entry<String, Object>) ob;
						if (en.getValue() instanceof Map) {
							Map<String, Object> j = (Map<String, Object>) entry.getValue();
							for (Object e : j.entrySet()) {
								Map.Entry<String, Object> ent = (Map.Entry<String, Object>) e;
								if (ent.getValue() instanceof Map) {
									Map<String, Object> ja = (Map<String, Object>) ent.getValue();
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

	@Override
	public Map<String, Object> getValues(boolean deep) {
		Map<String, Object> map = new HashMap<>();
		for (Object o : SOURCE.entrySet()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) o;
			if (deep) {
				if (entry.getValue() instanceof Map) {
					Map<String, Object> obj = (Map<String, Object>) entry.getValue();
					for (Object ob : obj.entrySet()) {
						Map.Entry<String, Object> en = (Map.Entry<String, Object>) ob;
						if (en.getValue() instanceof Map) {
							Map<String, Object> j = (Map<String, Object>) entry.getValue();
							for (Object e : j.entrySet()) {
								Map.Entry<String, Object> ent = (Map.Entry<String, Object>) e;
								if (ent.getValue() instanceof Map) {
									Map<String, Object> ja = (Map<String, Object>) ent.getValue();
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
		String[] a = ke.split("\\.");
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
		String[] a = key.split("\\.");
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
}
