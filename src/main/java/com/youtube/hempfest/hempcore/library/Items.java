package com.youtube.hempfest.hempcore.library;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public final class Items {
    private static final Map<String, Material> MATERIAL_ALIAS = new HashMap<>();

    static {
        for (Material material : Material.values()) {
            MATERIAL_ALIAS.put(material.name().toLowerCase().replace("_", ""), material);
        }
    }

    public static Material getMaterial(String name) {
        return MATERIAL_ALIAS.get(name.toLowerCase().replaceAll("_", ""));
    }

    private Items() {
    }
}
