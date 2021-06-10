package com.github.sanctum.labyrinth.library;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.EntityType;

/**
 * @author Hempfest
 */
public final class Entities {
	private static final Map<String, EntityType> TYPE_MAP = new HashMap<>();

	static {
		for (EntityType type : EntityType.values()) {
			TYPE_MAP.put(type.name().toLowerCase().replace("_", ""), type);
		}
	}

	/**
	 * Search for an entity type result and ignore all case sensitivity and underscores
	 * usually otherwise required.
	 *
	 * @param name The name of the entity to look for disregarding caps and underscores
	 * @return The desired entity type if not null.
	 */
	public static EntityType getEntity(String name) {
		return TYPE_MAP.get(name.toLowerCase().replaceAll("_", ""));
	}

	private Entities() {
	}
}
