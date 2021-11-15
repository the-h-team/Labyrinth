package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.data.ServiceManager;
import com.github.sanctum.labyrinth.data.container.KeyedServiceManager;
import com.github.sanctum.labyrinth.library.ItemCompost;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * The main API of Labyrinth provided by the plugin.
 */
public interface LabyrinthAPI extends VentService, TaskService, RecordingService, MessagingService, CooldownService, ActionComponentService, PersistentDataService, LegacyCheckService {
    /**
     * Get a Logger for messaging the console as Labyrinth.
     *
     * @return Labyrinth's logger
     */
    default Logger getLogger() {
        return getPluginInstance().getLogger();
    }

    /**
     * @return An object used for removing items efficiently from inventories.
     */
    ItemCompost getItemComposter();

    KeyedServiceManager<Plugin> getServicesManager();

    ServiceManager getServiceManager();

    /**
     * <strong>Library</strong> instance of the Labyrinth Bukkit plugin.
     * <p>
     * <strong><em>In most cases you should not use this method!</em></strong>
     * Please look over the other methods provided by this object.
     *
     * @return Plugin instance
     * @apiNote This method is not to be used by third parties!
     */
    Plugin getPluginInstance();
}
