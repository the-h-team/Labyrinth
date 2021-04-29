package com.github.sanctum.labyrinth.library;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Items {
    private static final Map<String, Material> MATERIAL_ALIAS = new HashMap<>();

    static {
        for (Material material : Material.values()) {
            MATERIAL_ALIAS.put(material.name().toLowerCase().replace("_", ""), material);
        }
    }

    /**
     * Query for a material by its name ignoring cap sensitivity and underscores.
     *
     * @param name The material to search for disregarding caps and underscores.
     * @return A material from query or null.
     */
    public static Material getMaterial(String name) {
        return MATERIAL_ALIAS.get(name.toLowerCase().replaceAll("_", ""));
    }

    /**
     * Craft an item with a desired display name automatically color translated.
     *
     * @param type The type of item to make
     * @param name The display name of the item.
     * @return The requested named item.
     */
    public static ItemStack getItem(Material type, String name) {
        ItemStack i = new ItemStack(type);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(StringUtils.use(name).translate());
        i.setItemMeta(meta);
        return i;
    }

    private Items() {
    }
}
