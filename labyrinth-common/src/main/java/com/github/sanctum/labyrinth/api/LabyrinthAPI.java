package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.data.ServiceManager;
import com.github.sanctum.labyrinth.data.container.KeyedServiceManager;
import com.github.sanctum.labyrinth.data.reload.PrintManager;
import com.github.sanctum.labyrinth.library.ItemCompost;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * ▄▄▌***▄▄▄·*▄▄▄▄·**▄·*▄▌▄▄▄**▪***▐*▄*▄▄▄▄▄*▄*.▄
 * ██•**▐█*▀█*▐█*▀█▪▐█▪██▌▀▄*█·██*•█▌▐█•██**██▪▐█
 * ██▪**▄█▀▀█*▐█▀▀█▄▐█▌▐█▪▐▀▀▄*▐█·▐█▐▐▌*▐█.▪██▀▐█
 * ▐█▌▐▌▐█*▪▐▌██▄▪▐█*▐█▀·.▐█•█▌▐█▌██▐█▌*▐█▌·██▌▐▀
 * .▀▀▀**▀**▀*·▀▀▀▀***▀*•*.▀**▀▀▀▀▀▀*█▪*▀▀▀*▀▀▀*·
 * Copyright (C) 2021 <strong>Sanctum</strong>
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * </p>
 * -
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * </p>
 * -
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 * </p>
 * Sanctum, hereby disclaims all copyright interest in the original features of this spigot library.
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
