package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.command.CommandVisibilityCalculation;
import java.util.HashMap;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Easily extract data from the server's internal command map.
 *
 * @author ms5984
 */
public final class CommandUtils {
    private static CommandUtils instance;
    private static CommandMap map;
    private final Map<String, Command> commandMappings;
    private static final Map<String, CommandVisibilityCalculation> calculations = new HashMap<>();

    private CommandUtils(LabyrinthAPI labyrinth) {
        try {
            final Field commandMapField = labyrinth.getPluginInstance().getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            final CommandMap commandMap = (CommandMap) commandMapField.get(labyrinth.getPluginInstance().getServer());
            map = commandMap;
            final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            //noinspection unchecked
            commandMappings = (Map<String, Command>) knownCommandsField.get(commandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to start CommandUtils!");
        }
    }

    public static Map<String, CommandVisibilityCalculation> getVisibilityCalculations() {
        return Collections.unmodifiableMap(calculations);
    }

    /**
     * Get an exhaustive list that includes ALL command labels, even
     * those that represent aliases of the same command.
     *
     * @return all registered command labels
     */
    public static Set<String> getServerCommandListing() {

        return Collections.unmodifiableSet(instance.commandMappings.keySet());
    }

    /**
     * Get a Command object directly from the server command map by its label.
     *
     * @param name label of the command
     * @return Command object if found or null
     */
    public static @Nullable Command getCommandByLabel(String name) {
        return instance.commandMappings.get(name);
    }

    public static void register(Command command, CommandVisibilityCalculation calculation) {
        calculations.put(command.getLabel(), calculation);
        Plugin holder = JavaPlugin.getProvidingPlugin(command.getClass());
        for (String alias : command.getAliases()) {
            calculations.put(holder.getName().toLowerCase(Locale.ROOT) + ":" + alias.toLowerCase(Locale.ROOT), calculation);
        }
    }

    public static void unregister(Command command) {register(null, player -> true);
        instance.commandMappings.remove(command.getName());
        for (String alias : command.getAliases()) {
            if (instance.commandMappings.containsKey(alias) && instance.commandMappings.get(alias).getAliases().contains(alias)) {
                instance.commandMappings.remove(alias);
            }

        }
        command.unregister(map);
    }

    public static void initialize(LabyrinthAPI labyrinth) throws IllegalStateException {
        if (instance != null) throw new IllegalStateException("CommandUtils already loaded!");
        instance = new CommandUtils(labyrinth);
    }
}
