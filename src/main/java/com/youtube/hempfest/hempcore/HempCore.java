package com.youtube.hempfest.hempcore;

import com.google.common.collect.Sets;
import com.youtube.hempfest.hempcore.data.Config;
import com.youtube.hempfest.hempcore.data.DataContainer;
import com.youtube.hempfest.hempcore.data.VaultHook;
import com.youtube.hempfest.hempcore.gui.GuiLibrary;
import com.youtube.hempfest.hempcore.gui.Menu;
import java.io.InputStream;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class HempCore extends JavaPlugin implements Listener {


    private static HempCore instance;

    private static final HashMap<Player, GuiLibrary> guiManager = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        run();
        Config main = new Config("Config", "Configuration");
        if (main.getConfig().getBoolean("use-click-event")) {
            Bukkit.getPluginManager().registerEvents(this, this);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> new VaultHook(this), 5);
        boolean success;
        try {
            getLogger().info("- Attempting automatic data container query process..");
            DataContainer.querySaved();
            success = true;
        } catch (NullPointerException e) {
            getLogger().info("- Process failed. No directory found to process.");
            getLogger().info("- Store a new instance of data for query to take effect on enable.");
            success = false;
        }
        if (success) {
            if (DataContainer.get().length == 0) {
                success = false;
                getLogger().info("- Process failed. No data found to process.");
            }
            getLogger().info("- Query success! All found meta cached. (" + DataContainer.get().length + ")");
        } else {
            getLogger().info("- Query failed! (SEE ABOVE FOR INFO)");
        }
    }

    @Override
    public void onDisable() {
        guiManager.clear();
    }

    public static HempCore getInstance() {
        return instance;
    }

    public static GuiLibrary guiManager(Player p) {
        GuiLibrary gui;
        if (!(guiManager.containsKey(p))) {


            gui = new GuiLibrary(p);
            guiManager.put(p, gui);

            return gui;
        } else {
            return guiManager.get(p);
        }
    }

    private void run() {
        Config main = new Config("Config", "Configuration");
        if (!main.exists()) {
            InputStream is = getResource("Config.yml");
            Config.copy(is, main.getFile());
        }
    }

    // GUI interact event
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMenuClick(InventoryClickEvent e) {

        InventoryHolder holder = e.getInventory().getHolder();
        // If the inventory holder of the inventory clicked on
        // is an instance of Menu, then gg. The reason that
        // an InventoryHolder can be a Menu is because our Menu
        // class implements InventoryHolder!!
        try {
            if (holder instanceof Menu) {
                e.setCancelled(true); // prevent them from fucking with the inventory
                if (e.getCurrentItem() == null) { // deal with null exceptions
                    return;
                }
                // Since we know our inventory holder is a menu, get the Menu Object representing
                // the menu we clicked on
                Menu menu = (Menu) holder;
                // Call the handleMenu object which takes the event and processes it
                menu.handleMenu(e);
            }
        } catch (AuthorNagException ignored) {
        }
    }


}
