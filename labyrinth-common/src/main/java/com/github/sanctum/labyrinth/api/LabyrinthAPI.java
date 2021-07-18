package com.github.sanctum.labyrinth.api;

import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public interface LabyrinthAPI extends VentService, TaskService, CooldownService, ActionComponentService, PersistentDataService, LegacyCheckService {
    /**
     * Get a Logger for messaging the console as Labyrinth.
     *
     * @return Labyrinth's logger
     */
    default Logger getLogger() {
        return getPluginInstance().getLogger();
    }

    /**
     * <strong>Library</strong> instance of the Labyrinth Bukkit plugin.
     * <p>
     * <strong><em>In most cases you should not use this method!</em></strong>
     * Please look over the other methods provided by this object.
     *
     * @return Plugin instance
     */
    Plugin getPluginInstance();
}
