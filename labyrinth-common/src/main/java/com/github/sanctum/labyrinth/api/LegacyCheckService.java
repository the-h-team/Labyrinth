package com.github.sanctum.labyrinth.api;

import org.bukkit.Bukkit;

/**
 * Detects legacy server environments.
 */
public interface LegacyCheckService extends Service {
    /**
     * Check if the environment of the server is legacy.
     *
     * @return true if the server version is 1.13 or lower
     */
    default boolean isLegacy() {
        return Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9")
                || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11")
                || Bukkit.getVersion().contains("1.12") || Bukkit.getVersion().contains("1.13");
    }

    /**
     * Check if the environment of the server is newer.
     *
     * @return true if the server version is 1.16 or higher
     */
    default boolean isNew() {
        return Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17");
    }

    /**
     * Check if due to the environment of the server
     * the LegacyConfigLocation utility must be loaded.
     *
     * @return if version 1.14 or lower (needs util)
     */
    default boolean requiresLocationLibrary() {
        return isLegacy() || Bukkit.getVersion().contains("1.14");
    }
}
