package com.github.sanctum.labyrinth.data.reload;

import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.panther.util.Deployable;
import com.github.sanctum.panther.util.EasyTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PrintManager {

	private final Map<String, FingerPrint> prints = new HashMap<>();

	public boolean register(FingerPrint print) {
		if (prints.containsKey(print.getKey().toString())) return false;
		prints.put(print.getKey().toString(), print);
		return true;
	}

	public boolean register(FingerMap objects, NamespacedKey key) {
		if (prints.containsKey(key.toString())) return false;
		FingerPrint print = new FingerPrint() {

			final Map<String, Object> map = new HashMap<>();

			{
				map.putAll(objects.accept());
			}

			@Override
			public @NotNull NamespacedKey getKey() {
				return key;
			}

			@Override
			public Object get(String key) {
				return map.get(key);
			}

			@Override
			public boolean getBoolean(String key) {
				if (map.containsKey(key)) {
					return new EasyTypeAdapter<Boolean>().cast(map.get(key));
				}
				return false;
			}

			@Override
			public String getString(String key) {
				if (map.containsKey(key)) {
					if (String.class.isAssignableFrom(map.get(key).getClass())) {
						return new EasyTypeAdapter<String>().cast(map.get(key));
					}
				}
				return null;
			}

			@Override
			public @NotNull Number getNumber(String key) {
				if (map.containsKey(key)) {
					if (Number.class.isAssignableFrom(map.get(key).getClass())) {
						return (Number) map.get(key);
					}
				}
				return 0.0;
			}

			@Override
			public @NotNull List<String> getStringList(String key) {
				if (map.containsKey(key)) {
					if (List.class.isAssignableFrom(map.get(key).getClass())) {
						if (String.class.isAssignableFrom(((List<?>)map.get(key)).get(0).getClass())) {
							return new EasyTypeAdapter<List<String>>().cast(map.get(key));
						}
					}
				}
				return new ArrayList<>();
			}

			@Override
			public @NotNull Deployable<Map<String, Object>> clear() {
				return Deployable.of(map, Map::clear, 0);
			}

			@Override
			public @NotNull Deployable<FingerPrint> reload(String key) {
				return Deployable.of(() -> {
					Map<String, Object> test = objects.accept();
					if (test.containsKey(key)) {
						map.put(key, test.get(key));
					} else throw new IllegalArgumentException("Cannot reload non-existent directories!");
					return this;
				}, 0);
			}

			@Override
			public @NotNull Deployable<FingerPrint> reload() {
				return Deployable.of(() -> {
					clear().deploy(m -> m.putAll(objects.accept()));
					return this;
				}, 0);
			}
		};
		prints.put(key.toString(), print);
		return true;
	}

	public boolean remove(NamespacedKey key) {
		if (!prints.containsKey(key.toString())) return false;
		prints.remove(key.toString());
		return true;
	}

	public boolean remove(FingerPrint print) {
		return remove(print.getKey());
	}

	public FingerPrint getPrint(NamespacedKey key) {
		return prints.get(key.toString());
	}

	public Set<FingerPrint> getPrints(Plugin plugin) {
		return prints.entrySet().stream().filter(stringFingerPrintEntry -> stringFingerPrintEntry.getKey().contains(plugin.getName().toLowerCase(Locale.ROOT))).map(Map.Entry::getValue).collect(Collectors.toSet());
	}

}
