package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MapDecompressionUtils {

	static MapDecompressionUtils instance;

	MapDecompressionUtils(){}

	private String appendChild(String key, char divider, String text) {
		return key != null ? key + divider + text : text;
	}

	public MapDecompression decompress(@NotNull Iterable<? extends Map.Entry<String, Object>> iterable, char divider, @Nullable String parentKey) {
		return new MapDecompression() {
			@Override
			public Set<String> toSet() {
				return decompressNormalKeys(iterable, divider, parentKey);
			}

			@Override
			public LabyrinthCollection<String> toLabyrinthSet() {
				return decompressLabyrinthKeys(iterable, divider, parentKey);
			}

			@Override
			public Map<String, Object> toMap() {
				return decompressNormalValues(iterable, divider, parentKey);
			}

			@Override
			public LabyrinthMap<String, Object> toLabyrinthMap() {
				return decompressLabyrinthValues(iterable, divider, parentKey);
			}
		};
	}

	Set<String> decompressNormalKeys(@NotNull Iterable<? extends Map.Entry<String, Object>> iterable, char divider, @Nullable String parent) {
		Set<String> set = new HashSet<>();
		for (Map.Entry<String, Object> entry : iterable) {
			if (entry.getValue() instanceof Map) {
				set.addAll(decompressNormalKeys(((Map<String, Object>) entry.getValue()).entrySet(), divider, appendChild(parent, divider, entry.getKey())));
			} else {
				if (entry.getValue() instanceof LabyrinthMap) {
					decompressLabyrinthKeys(((LabyrinthMap<String, Object>) entry.getValue()).entries(), divider, appendChild(parent, divider, entry.getKey())).forEach(set::add);
				} else {
					set.add(appendChild(parent, divider, entry.getKey()));
				}
			}
		}
		return set;
	}

	Map<String, Object> decompressNormalValues(@NotNull Iterable<? extends Map.Entry<String, Object>> iterable, char divider, @Nullable String parent) {
		Map<String, Object> m = new HashMap<>();
		for (Map.Entry<String, Object> entry : iterable) {
			if (entry.getValue() instanceof Map) {
				m.putAll(decompressNormalValues(((Map<String, Object>) entry.getValue()).entrySet(), divider, appendChild(parent, divider, entry.getKey())));
			} else {
				if (entry.getValue() instanceof LabyrinthMap) {
					decompressLabyrinthValues(((LabyrinthMap<String, Object>) entry.getValue()).entries(), divider, appendChild(parent, divider, entry.getKey())).forEach(e -> m.put(e.getKey(), e.getValue()));
				} else {
					m.put(appendChild(parent, divider, entry.getKey()), entry.getValue());
				}
			}
		}
		return m;
	}

	LabyrinthCollection<String> decompressLabyrinthKeys(@NotNull Iterable<? extends Map.Entry<String, Object>> iterable, char divider, @Nullable String parent) {
		LabyrinthCollection<String> set = new LabyrinthSet<>();
		for (Map.Entry<String, Object> entry : iterable) {
			if (entry.getValue() instanceof LabyrinthMap) {
				set.addAll(decompressLabyrinthKeys(((LabyrinthMap<String, Object>) entry.getValue()).entries(), divider, appendChild(parent, divider, entry.getKey())));
			} else {
				if (entry.getValue() instanceof Map) {
					set.addAll(decompressNormalKeys(((Map<String, Object>) entry.getValue()).entrySet(), divider, appendChild(parent, divider, entry.getKey())));
				} else {
					set.add(appendChild(parent, divider, entry.getKey()));
				}
			}
		}
		return set;
	}

	LabyrinthMap<String, Object> decompressLabyrinthValues(@NotNull Iterable<? extends Map.Entry<String, Object>> iterable, char divider, @Nullable String parent) {
		LabyrinthMap<String, Object> m = new LabyrinthEntryMap<>();
		for (Map.Entry<String, Object> entry : iterable) {
			if (entry.getValue() instanceof LabyrinthMap) {
				decompressLabyrinthValues(((LabyrinthMap<String, Object>) entry.getValue()).entries(), divider, appendChild(parent, divider, entry.getKey())).forEach(e -> m.put(e.getKey(), e.getValue()));
			} else {
				if (entry.getValue() instanceof Map) {
					decompressNormalValues(((Map<String, Object>) entry.getValue()).entrySet(), divider, appendChild(parent, divider, entry.getKey())).forEach(m::put);
				} else {
					m.put(appendChild(parent, divider, entry.getKey()), entry.getValue());
				}
			}
		}
		return m;
	}

	public static @NotNull MapDecompressionUtils getInstance() {
		return instance != null ? instance : (instance = new MapDecompressionUtils());
	}

}
