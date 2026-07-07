package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.data.ServiceManager;
import com.github.sanctum.labyrinth.data.container.MemoryBank;
import com.github.sanctum.labyrinth.data.reload.PrintManager;
import com.github.sanctum.labyrinth.data.service.*;
import com.github.sanctum.labyrinth.common.ItemCompost;
import com.github.sanctum.panther.annotation.Note;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * <pre>
 * ▄▄▌***▄▄▄·*▄▄▄▄·**▄·*▄▌▄▄▄**▪***▐*▄*▄▄▄▄▄*▄*.▄
 * ██•**▐█*▀█*▐█*▀█▪▐█▪██▌▀▄*█·██*•█▌▐█•██**██▪▐█
 * ██▪**▄█▀▀█*▐█▀▀█▄▐█▌▐█▪▐▀▀▄*▐█·▐█▐▐▌*▐█.▪██▀▐█
 * ▐█▌▐▌▐█*▪▐▌██▄▪▐█*▐█▀·.▐█•█▌▐█▌██▐█▌*▐█▌·██▌▐▀
 * .▀▀▀**▀**▀*·▀▀▀▀***▀*•*.▀**▀▀▀▀▀▀*█▪*▀▀▀*▀▀▀*·
 * </pre>
 * Copyright (c) 2020-2023 <strong>Sanctum</strong>
 */
public interface LabyrinthAPI extends VentService, TaskService, RecordService, MessagingService, CooldownService, ActionComponentService, PersistentDataService, LegacyCheckService {
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

    /**
     * @return A manager for data footprints.
     */
    @Note("Don't store your footprints here! Make your own manager with PrintManager")
    PrintManager getPrintManager();

    /**
     * @return A service manager for plugin objects stored in memory
     */
    MemoryBank<Plugin> getMemoryBank();

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
