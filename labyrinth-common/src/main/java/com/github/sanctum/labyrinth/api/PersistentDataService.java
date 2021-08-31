package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Manages persistent data container access.
 */
public interface PersistentDataService extends Service {
    /**
     * Get a list of all containers associated with a specified plugin.
     * <p>
     * Containers must have been initialized <strong>at least once</strong>
     * prior to access in this view.
     *
     * @return a list of containers associated with the specified plugin
     */
    @NotNull List<PersistentContainer> getContainers(Plugin plugin);

    /**
     * Operate on a custom persistent data container at the specified coordinates.
     *
     * @param namespacedKey the namespaced key for this component
     * @return the existing data container or a new instance
     */
    @NotNull PersistentContainer getContainer(@NotNull NamespacedKey namespacedKey);

}
