package com.github.sanctum.labyrinth.data.container;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.NamespacedKey;

public class DataParser {

	private static final List<DataComponent<?>> COMPONENTS = new LinkedList<>();

	public static <V> DataComponent<V> test(Class<V> path, NamespacedKey key) {
		for (DataComponent<?> component : COMPONENTS) {
			if (component.getPrimative().isAssignableFrom(path)) {
				if (component.getKey().equals(key)) {
					return (DataComponent<V>) component;
				}
			}
		}
		DataComponent<V> component = new DataComponent<>(path, key);
		COMPONENTS.add(component);
		return component;
	}

	public static List<DataComponent<?>> getDataComponents() {
		return COMPONENTS;
	}
}
