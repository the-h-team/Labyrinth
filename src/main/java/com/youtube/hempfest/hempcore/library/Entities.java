package com.youtube.hempfest.hempcore.library;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public final class Entities {
    private static final Map<String, EntityType> MATERIAL_ALIAS = new HashMap<>();

    static {
        for (EntityType type : EntityType.values()) {
            MATERIAL_ALIAS.put(type.name().toLowerCase().replace("_", ""), type);
        }
    }

    public static EntityType getEntity(String name) {
        return MATERIAL_ALIAS.get(name.toLowerCase().replaceAll("_", ""));
    }

    private Entities() {
    }
}
