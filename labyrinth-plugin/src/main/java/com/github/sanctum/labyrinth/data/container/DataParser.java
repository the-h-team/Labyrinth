package com.github.sanctum.labyrinth.data.container;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.NamespacedKey;

public class DataParser {

	private static final List<DataComponent> COMPONENTS = new LinkedList<>();

	/**
	 * Operate on a custom persistent data container using a specified name space.
	 *
	 * @param key The name space for this component.
	 * @return The desired data container.
	 */
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

	/**
	 * @return All cached data containers.
	 */
	public static List<DataComponent> getDataComponents() {
		return COMPONENTS;
	}
}
