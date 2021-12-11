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
        return Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18");
    }

    /**
     * This version check mirrors {@link LegacyCheckService#isLegacy()} but adds the villager and pillager
     * update on top, versions in this range require a labyrinth provided {@link org.bukkit.configuration.serialization.ConfigurationSerializable} implementation to allow
     * proper yaml location/flat-file conversions.
     *
     * @return true if the server version is 1.14 or lower.
     */
    default boolean isLegacyVillager() {
        return isLegacy() || Bukkit.getVersion().contains("1.14");
    }
}
