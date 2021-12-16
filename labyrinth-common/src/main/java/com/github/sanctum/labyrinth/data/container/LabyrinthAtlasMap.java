package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.data.MemorySpace;
import com.github.sanctum.labyrinth.data.Node;
import com.github.sanctum.labyrinth.data.ReplaceableKeyedValue;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LabyrinthAtlasMap implements LabyrinthAtlas {

	private final LabyrinthMap<String, Object> SOURCE = new LabyrinthEntryMap<>();
	protected final LabyrinthMap<String, MemorySpace> QUERY = new LabyrinthEntryMap<>();

	@Override
	public boolean isNode(String key) {
		String[] a = key.split("\\.");
		String k = a[Math.max(0, a.length - 1)];
		LabyrinthMap<String, Object> o = SOURCE;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			Object obj = o.get(pathKey);
			if (obj instanceof Map) {
				LabyrinthMap<String, Object> js = (LabyrinthMap<String, Object>) obj;
				if (js.containsKey(k)) {
					return js.get(k) instanceof LabyrinthMap;
				} else {
					o = js;
				}
			}
		}
		return o.get(k) instanceof Map;
	}

	@Override
	public Node getNode(String key) {
		return (Node) QUERY.entries().stream().filter(e -> e.getKey().equals(key)).map(Map.Entry::getValue).findFirst().orElseGet(() -> {
			LabyrinthAtlasNode n = new LabyrinthAtlasNode(key, this);
			QUERY.put(key, n);
			return n;
		});
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		Set<String> keys = new HashSet<>();
		for (Object o : SOURCE.entries()) {
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
		for (Object o : SOURCE.entries()) {
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
	public boolean containsKey(String key) {
		return get(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public Object get(String key) {
		String ke = key;
		String[] a = ke.split("\\.");
		String k = a[Math.max(0, a.length - 1)];
		LabyrinthMap<String, Object> o = SOURCE;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			Object obj = o.get(pathKey);
			if (obj instanceof Map) {
				LabyrinthMap<String, Object> js = (LabyrinthMap<String, Object>) obj;
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
		LabyrinthMap<String, Object> ob = SOURCE;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			Object os = ob.get(pathKey);
			if (os instanceof LabyrinthMap) {
				ob = (LabyrinthMap<String, Object>) os;
			} else {
				LabyrinthMap<String, Object> n = new LabyrinthEntryMap<>();
				ob.put(pathKey, n);
				ob = (LabyrinthMap<String, Object>) ob.get(pathKey);
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
	public boolean putAll(Iterable<Map.Entry<String, Object>> iterable) {
		return SOURCE.putAll(iterable);
	}

	@Override
	public boolean remove(String key) {
		return SOURCE.remove(key);
	}

	@Override
	public boolean removeAll(Iterable<Map.Entry<String, Object>> iterable) {
		return SOURCE.removeAll(iterable);
	}

	@Override
	public void clear() {
		SOURCE.clear();
	}

	@NotNull
	@Override
	public Iterator<ReplaceableKeyedValue<String, Object>> iterator() {
		return SOURCE.iterator();
	}
}
