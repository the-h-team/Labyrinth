package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.data.ReplaceableKeyedValue;
import com.github.sanctum.panther.container.PantherEntry;
import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherMap;
import com.github.sanctum.panther.file.MemorySpace;
import com.github.sanctum.panther.file.Node;
import com.github.sanctum.panther.util.MapDecompression;
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

	protected final PantherMap<String, Object> SOURCE = new PantherEntryMap<>();
	protected final PantherMap<String, MemorySpace> QUERY = new PantherEntryMap<>();
	protected final char divider;

	public LabyrinthAtlasMap() {
		this.divider = '.';
	}

	public LabyrinthAtlasMap(char divider) {
		this.divider = divider;
	}

	String dividerAdapt() {
		return divider == '.' ? "[" + divider + "]" : divider + "";
	}

	@Override
	public boolean isNode(String key) {
		String[] a = key.split(dividerAdapt());
		String k = a[Math.max(0, a.length - 1)];
		PantherMap<String, Object> o = SOURCE;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			Object obj = o.get(pathKey);
			if (obj instanceof Map) {
				PantherMap<String, Object> js = (PantherMap<String, Object>) obj;
				if (js.containsKey(k)) {
					return js.get(k) instanceof PantherMap;
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
		SOURCE.forEach(e -> {
			if (deep) {
				if (e.getValue() instanceof PantherMap) {
					MapDecompression.getInstance().decompress((PantherMap<String, Object>) e.getValue(), divider, null).toPantherSet().forEach(keys::add);
				} else {
					keys.add(e.getKey());
				}
			} else {
				keys.add(e.getKey());
			}
		});
		return keys;
	}

	@Override
	public Map<String, Object> getValues(boolean deep) {
		Map<String, Object> map = new HashMap<>();
		SOURCE.forEach(e -> {
			if (deep) {
				if (e.getValue() instanceof PantherMap) {
					MapDecompression.getInstance().decompress((PantherMap<String, Object>) e.getValue(), divider, null).toPantherMap().forEach(ev -> map.put(ev.getKey(), ev.getValue()));
				} else {
					map.put(e.getKey(), e.getValue());
				}
			} else {
				map.put(e.getKey(), e.getValue());
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
	public boolean containsKey(String key) {
		return get(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public Object get(String key) {
		String[] a = key.split(dividerAdapt());
		String k = a[Math.max(0, a.length - 1)];
		PantherMap<String, Object> o = SOURCE;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			Object obj = o.get(pathKey);
			if (obj instanceof Map) {
				PantherMap<String, Object> js = (PantherMap<String, Object>) obj;
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
		PantherMap<String, Object> ob = SOURCE;
		for (int i = 0; i < a.length - 1; i++) {
			String pathKey = a[i];
			Object os = ob.get(pathKey);
			if (os instanceof PantherMap) {
				ob = (PantherMap<String, Object>) os;
			} else {
				PantherMap<String, Object> n = new PantherEntryMap<>();
				ob.put(pathKey, n);
				ob = (PantherMap<String, Object>) ob.get(pathKey);
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
	public Iterator<PantherEntry.Modifiable<String, Object>> iterator() {
		return SOURCE.iterator();
	}

	@Override
	public char getDivider() {
		return divider;
	}
}
