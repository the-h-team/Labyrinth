package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Converts keys for a given platform.
 *
 * @since 1.9.4
 * @author ms5984
 */
public interface PlatformKeyService extends Service {
    /**
     * Allows for the conversion of a namespaced key to a native key, if possible.
     *
     * @param key a namespaced key
     * @return a platform-native key or null if not supported
     */
    @Nullable Object toNative(@NotNull NamespacedKey key);

    /**
     * Converts a native key to a namespaced key, if possible.
     *
     * @param key a platform-native key
     * @return a namespaced key or null if {@code key} not supported
     */
    @Nullable NamespacedKey fromNative(@NotNull Object key);
}
