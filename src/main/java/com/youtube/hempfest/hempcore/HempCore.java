package com.youtube.hempfest.hempcore;

import com.google.common.collect.Sets;
import com.youtube.hempfest.hempcore.gui.GuiLibrary;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
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

public final class HempCore extends JavaPlugin {


    private static HempCore instance;

    private static final HashMap<Player, GuiLibrary> guiManager = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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

}
