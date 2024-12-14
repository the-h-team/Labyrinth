package com.github.sanctum.skulls;

import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.panther.file.MemorySpace;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class SkullReferenceUtility {

    protected static List<CustomHead> heads = new ArrayList<>();
    protected static boolean loaded;

    /**
     * Load a custom head object into cache.
     *
     * @param object the custom head object to cache
     */
    public static void load(CustomHead object) {
        boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
        Material type = Items.findMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
        if (object.getItem().getType() != type)
            throw new IllegalStateException(object.getItem().getType().name() + " is not a direct representation of " + type.name());
        heads.add(object);
    }

    /**
     * Using {@link SkullReferenceUtility#newLoader(MemorySpace)} or {@link SkullReferenceUtility#newLoader(Plugin, String, String)} load configured head values
     * from your specified file location.
     *
     * @param loader the head loader instance
     */
    public static void load(SkullReferenceDocker loader) {
        for (Map.Entry<SkullReferenceTicket, ItemStack> entry : loader.map(false).additions.entrySet()) {
            load(entry.getKey().getName(), entry.getKey().getCategory(), entry.getValue());
        }
    }

    /**
     * Get a local player's head.
     * <p>
     * Results are cached. If a result is already found within the query it is instead returned.
     *
     * @param player the player to fetch
     * @return the head of the specified player or null if not found
     */
    public static @Nullable ItemStack getItem(OfflinePlayer player) {
        return heads.stream().filter(h -> h.getId().isPresent() && h.getId().get().equals(player.getUniqueId())).map(CustomHead::getItem).findFirst().orElseGet(() -> {
            SkullReferenceLookup search;
            try {
                search = new SkullReferenceLookup(player.getUniqueId());
            } catch (InvalidSkullReferenceException e) {
                return null;
            }
            CustomHead head = new DefaultHead(player.getName(), "Human", search.getResult(), player.getUniqueId());
            heads.add(head);
            return head.getItem();
        });
    }

    /**
     * Get the head of either a local player or one that has <strong>never</strong> logged onto the server before.
     * <p>
     * As long as the id has a valid link to a player from the mojang api results are cached.
     *
     * @param id a valid player id
     * @return the head of the specified user or null if not found
     */
    public static @Nullable ItemStack getItem(UUID id) {
        return heads.stream().filter(h -> h.getId().isPresent() && h.getId().get().equals(id)).map(CustomHead::getItem).findFirst().orElseGet(() -> {

            SkullReferenceLookup search;
            try {
                search = new SkullReferenceLookup(id);
            } catch (InvalidSkullReferenceException e) {
                return null;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(id);
            if (player.hasPlayedBefore()) {
                CustomHead head = new DefaultHead(player.getName(), "Human", search.getResult(), id);
                heads.add(head);
                return head.getItem();
            } else return null;
        });
    }

    /**
     * Get the head of either a local player or one that has <strong>never</strong> logged onto the server before.
     * <p>
     * As long as the id has a valid link to a player from the mojang api results are cached.
     *
     * @param name the name of the player
     * @return the head of the specified user or null if not found
     */
    public static @Nullable ItemStack getItem(String name) {
        return heads.stream().filter(h -> h.getName().equals(name)).map(CustomHead::getItem).findFirst().orElseGet(() -> {
            SkullReferenceLookup search;
            try {
                search = new SkullReferenceLookup(name);
            } catch (InvalidSkullReferenceException e) {
                return null;
            }
            CustomHead head = new DefaultHead(name, "Human", search.getResult());
            heads.add(head);
            return head.getItem();
        });
    }

    /**
     * Get the head of either a local player or one that has <strong>never</strong> logged onto the server before.
     * <p>
     * As long as the id has a valid link to a player from the mojang api results are cached.
     *
     * @param name the name of the player
     * @return the head of the specified user or null if not found
     */
    public static @Nullable CustomHead getHead(String name) {
        return heads.stream().filter(h -> h.getName().equals(name)).findFirst().orElseGet(() -> {
            SkullReferenceLookup search;
            try {
                search = new SkullReferenceLookup(name);
            } catch (InvalidSkullReferenceException e) {
                return null;
            }
            CustomHead head = new DefaultHead(name, "Human", search.getResult());
            heads.add(head);
            return head;
        });
    }

    /**
     * Get the head of either a local player or one that has <strong>never</strong> logged onto the server before.
     * <p>
     * As long as the id has a valid link to a player from the mojang api results are cached.
     *
     * @param id a valid player id
     * @return the head of the specified user or null if not found
     */
    public static @Nullable CustomHead getHead(UUID id) {
        return heads.stream().filter(h -> h.getId().isPresent() && h.getId().get().equals(id)).findFirst().orElseGet(() -> {
            SkullReferenceLookup search;
            try {
                search = new SkullReferenceLookup(id);
            } catch (InvalidSkullReferenceException e) {
                return null;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(id);
            if (player.hasPlayedBefore()) {
                CustomHead head = new DefaultHead(player.getName(), "Human", search.getResult(), id);
                heads.add(head);
                return head;
            } else return null;
        });
    }

    /**
     * Get a local player's head.
     * <p>
     * Results are cached. If a result is already found within the query it is instead returned.
     *
     * @param player the player to fetch
     * @return the head of the specified player or null if not found
     */
    public static @Nullable CustomHead getHead(OfflinePlayer player) {
        return heads.stream().filter(h -> h.getId().isPresent() && h.getId().get().equals(player.getUniqueId())).findFirst().orElseGet(() -> {
            SkullReferenceLookup search;
            try {
                search = new SkullReferenceLookup(player.getUniqueId());
            } catch (InvalidSkullReferenceException e) {
                return null;
            }
            CustomHead head = new DefaultHead(player.getName(), "Human", search.getResult(), player.getUniqueId());
            heads.add(head);
            return head;
        });
    }

    /**
     * Get all known custom head categories.
     *
     * @return a list of every known custom head category
     */
    public static List<String> getCategories() {
        return heads.stream().map(CustomHead::getCategory).collect(Collectors.toList());
    }

    /**
     * Get all known custom heads as an {@link ItemStack} specified by a category.
     * <p>
     * If the specified category isn't found an empty list is returned.
     *
     * @param category the valid category to search
     * @return all categorized heads--or an empty list if you are ill-informed
     */
    public static List<ItemStack> getCategory(String category) {
        if (!getCategories().contains(category)) {
            return new ArrayList<>();
        }
        return heads.stream().filter(h -> h.getCategory().equals(category)).map(CustomHead::getItem).collect(Collectors.toList());
    }

    /**
     * Get all currently cached custom heads.
     *
     * @return a list of <strong>ALL</strong> known custom heads
     */
    public static List<CustomHead> getHeads() {
        return Collections.unmodifiableList(heads);
    }

    /**
     * Assign the loading of additional configured head elements.
     *
     * @param plugin    the source plugin
     * @param fileName  the name of the file to use
     * @param directory the directory of the file
     * @return a new head loader instance
     */
    public static SkullReferenceDocker newLoader(Plugin plugin, String fileName, String directory) {
        return new SkullReferenceDocker(plugin, fileName, directory);
    }

    /**
     * Assign the loading of additional head elements.
     *
     * @param memory the memory space to source the additions from
     * @return a new head loader instance
     */
    public static SkullReferenceDocker newLoader(MemorySpace memory) {
        return new SkullReferenceDocker(memory);
    }

    /**
     * For internal use only!!
     *
     * @param name     the name of the custom head
     * @param category the category of the custom head
     * @param item     the skull item
     * @deprecated Use {@link SkullReferenceUtility#load(CustomHead)} to load already verified items.
     */
    private static void load(String name, String category, ItemStack item) {
        boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
        Material type = Items.findMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");

        if (item.getType() != type)
            throw new IllegalStateException(item.getType().name() + " is not a direct representation of " + type.name());

        load(new DefaultHead(name, category, item));
    }

    /**
     * Copy all lore from a targeted player head and change the owner to one specified.
     *
     * @param item the item to modify
     * @param name the name of the new owner
     * @return the modified ItemStack
     */
    public static ItemStack modifyItemStack(ItemStack item, String name) {
        boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
        Material type = Items.findMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");

        if (item.getType() != type)
            throw new IllegalStateException(item.getType().name() + " is not a direct representation of " + type.name());

        CustomHead head = getHead(name);
        return head != null ? new Item.Edit(head.getItem()).setTitle(item.getItemMeta() != null ? item.getItemMeta().getDisplayName() : head.getName()).setLore(item.getItemMeta() != null && item.getItemMeta().getLore() != null ? item.getItemMeta().getLore().toArray(new String[0]) : new String[]{""}).build() : item;

    }

}
