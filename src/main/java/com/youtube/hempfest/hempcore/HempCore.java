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

    static HashMap<Player, GuiLibrary> guiManager = new HashMap<>();

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

    public static void registerAllCommandsAutomatically(String packageName, Plugin plugin) {
        Set<Class<?>> classes = Sets.newHashSet();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(URLDecoder.decode(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (JarEntry jarEntry : Collections.list(jarFile.entries())) {
            String className = jarEntry.getName().replace("/", ".");
            if (className.startsWith(packageName) && className.endsWith(".class")) {
                Class<?> clazz;
                try {
                    clazz = Class.forName(className.substring(0, className.length() - 6));
                } catch (ClassNotFoundException e) {
                    getInstance().getLogger().severe("- Unable to find class" + className + "! Make sure you spelled it correctly when using the registerAllCommandsAutomatically method. See the error below for an exact line.");
                    break;
                }
                if (BukkitCommand.class.isAssignableFrom(clazz)) {
                    classes.add(clazz);
                }
            }
        }
        for (Class<?> aClass : classes) {
            try {
                BukkitCommand command = ((BukkitCommand) aClass.getDeclaredConstructor().newInstance());
                final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);

                final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
                commandMap.register(command.getLabel(), command);
                //registerCommand((BukkitCommand) aClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException e) {
                getInstance().getLogger().severe("- Unable to cast BukkitCommand to the class " + aClass.getName() + ". This likely means you are not extending BukkitCommand for your command class.");
                e.printStackTrace();
                break;
            }
        }
    }

    //OLD METHOD FOR TESTING PURPOSES
    /*
    private static Set<Class<?>> getCommandExecutorsInPackage(String packageName, Plugin plugin) {
        Set<Class<?>> classes = Sets.newHashSet();
        JarFile jarFile;
        try {
            jarFile = new JarFile(URLDecoder.decode(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return classes;
        }
        for (JarEntry jarEntry : Collections.list(jarFile.entries())) {
            String className = jarEntry.getName().replace("/", ".");
            if (className.startsWith(packageName) && className.endsWith(".class")) {
                Class<?> clazz;
                try {
                    clazz = Class.forName(className.substring(0, className.length() - 6));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    continue;
                }
                if (BukkitCommand.class.isAssignableFrom(clazz)) {
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }

     */

}
