package com.github.sanctum.labyrinth.gui.basalt.internal.event;

import com.github.sanctum.labyrinth.gui.basalt.InventoryContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class NervousSystem implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof InventoryContainer)) return;

        InventoryContainer container = (InventoryContainer) event.getInventory().getHolder();



        // process click information
        // ClickProcessor#run

    }

}
