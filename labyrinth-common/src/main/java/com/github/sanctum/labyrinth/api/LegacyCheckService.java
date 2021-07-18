package com.github.sanctum.labyrinth.api;

import org.bukkit.Bukkit;

public interface LegacyCheckService {
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
}
