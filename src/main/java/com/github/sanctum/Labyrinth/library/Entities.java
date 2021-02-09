package com.github.sanctum.Labyrinth.library;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.EntityType;

public final class Entities {
    private static final Map<String, EntityType> TYPE_MAP = new HashMap<>();

    static {
        for (EntityType type : EntityType.values()) {
            TYPE_MAP.put(type.name().toLowerCase().replace("_", ""), type);
        }
    }

    public static EntityType getEntity(String name) {
        return TYPE_MAP.get(name.toLowerCase().replaceAll("_", ""));
    }

    private Entities() {
    }
}
