package com.github.sanctum.labyrinth.library;

import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

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
	 * Search for an entity type result ignoring case
	 * typical delimiting underscores.
	 *
	 * @param name name of the entity; disregards case and underscores
	 * @return the desired EntityType or null
	 */
	public static EntityType getEntity(String name) {
		return TYPE_MAP.get(name.toLowerCase().replaceAll("_", ""));
	}

	private Entities() {
	}
}
