package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Converts keys for the Bukkit platform.
 *
 * @since 1.9.4
 * @author ms5984
 */
public final class BukkitPlatformKeyService implements PlatformKeyService {
    @Override
    public @Nullable org.bukkit.NamespacedKey toNative(@NotNull NamespacedKey key) {
        try {
            Class.forName("org.bukkit.NamespacedKey", false, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
        //noinspection deprecation
        return new org.bukkit.NamespacedKey(key.getNamespace(), key.getKey());
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable NamespacedKey fromNative(@NotNull Object key) {
        if (!(key instanceof org.bukkit.NamespacedKey)) return null;
        org.bukkit.NamespacedKey bukkitKey = (org.bukkit.NamespacedKey) key;
        return new NamespacedKey(bukkitKey.getNamespace(), bukkitKey.getKey());
    }
}
