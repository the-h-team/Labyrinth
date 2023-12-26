package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.data.ServiceManager;
import com.github.sanctum.labyrinth.data.container.KeyedServiceManager;
import com.github.sanctum.labyrinth.data.reload.PrintManager;
import com.github.sanctum.labyrinth.library.ItemCompost;
import com.github.sanctum.panther.annotation.Note;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.logging.Logger;

/**
 * <pre>
 * ▄▄▌***▄▄▄·*▄▄▄▄·**▄·*▄▌▄▄▄**▪***▐*▄*▄▄▄▄▄*▄*.▄
 * ██•**▐█*▀█*▐█*▀█▪▐█▪██▌▀▄*█·██*•█▌▐█•██**██▪▐█
 * ██▪**▄█▀▀█*▐█▀▀█▄▐█▌▐█▪▐▀▀▄*▐█·▐█▐▐▌*▐█.▪██▀▐█
 * ▐█▌▐▌▐█*▪▐▌██▄▪▐█*▐█▀·.▐█•█▌▐█▌██▐█▌*▐█▌·██▌▐▀
 * .▀▀▀**▀**▀*·▀▀▀▀***▀*•*.▀**▀▀▀▀▀▀*█▪*▀▀▀*▀▀▀*·
 * </pre>
 * The Labyrinth API.
 * <p>
 * Copyright (c) 2020-2023 Sanctum
 */
@ApiStatus.NonExtendable
public interface LabyrinthAPI extends VentService, TaskService, RecordingService, MessagingService, CooldownService, ActionComponentService, PersistentDataService, LegacyCheckService {
    /**
     * Gets a logger for messaging the console as Labyrinth.
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

    /**
     * @return A manager for data footprints.
     */
    @Note("Don't store your footprints here! Make your own manager with PrintManager")
    PrintManager getLocalPrintManager();

    /**
     * @return A service manager for plugin's
     */
    KeyedServiceManager<Plugin> getServicesManager();

    /**
     * @return A general purpose service manager for {@link Service}'s
     */
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
