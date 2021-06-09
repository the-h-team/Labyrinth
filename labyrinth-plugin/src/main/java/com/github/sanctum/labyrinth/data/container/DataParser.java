package com.github.sanctum.labyrinth.data.container;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.NamespacedKey;

public class DataParser {

	private static final List<DataComponent> COMPONENTS = new LinkedList<>();

	public static DataComponent test(NamespacedKey key) {
		for (DataComponent component : COMPONENTS) {
			if (component.getKey().equals(key)) {
				return component;
			}
		}
		DataComponent component = new DataComponent(key);
		COMPONENTS.add(component);
		return component;
	}

	public static List<DataComponent> getDataComponents() {
		return COMPONENTS;
	}
}
