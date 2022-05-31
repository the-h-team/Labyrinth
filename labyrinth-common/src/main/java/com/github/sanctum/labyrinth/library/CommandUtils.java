package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Ordinal;
import com.github.sanctum.labyrinth.command.CommandVisibilityCalculation;
import com.github.sanctum.labyrinth.command.SubCommandList;
import com.github.sanctum.labyrinth.command.SubCommand;
import com.github.sanctum.labyrinth.data.SimpleKeyedValue;
import com.github.sanctum.labyrinth.data.container.ImmutableLabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.ImmutableLabyrinthMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthSet;
import com.github.sanctum.labyrinth.interfacing.OrdinalProcedure;
import java.util.Optional;
import java.util.function.Function;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Easily extract data from the server's internal command map.
 *
 * @author ms5984, Hempfest
 */
public final class CommandUtils {

    static Map<String, Command> commands;
    static CommandMap commandMap;
    private static final LabyrinthCollection<CommandVisibilityCalculation> calculations = new LabyrinthSet<>();
    private static final LabyrinthMap<String, SubCommandList> wrappers = new LabyrinthEntryMap<>();

    static {
        Plugin main = LabyrinthProvider.getInstance().getPluginInstance();
        Map<String, Command> commandMappings;
        CommandMap theMap;
        try {
            final Field commandMapField = main.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            final CommandMap commandMap = (CommandMap) commandMapField.get(main.getServer());
            theMap = commandMap;
            final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            //noinspection unchecked
            commandMappings = (Map<String, Command>) knownCommandsField.get(commandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to initialize CommandUtils!");
        }
        commandMap = theMap;
        commands = commandMappings;
    }

    /**
     * @return An immutable map of all registered sub command holders.
     */
    public static LabyrinthMap<String, SubCommandList> getSubCommandHolders() {
        return ImmutableLabyrinthMap.of(wrappers);
    }

    /**
     * @return An immutable collection of command visibility calculations.
     */
    public static LabyrinthCollection<CommandVisibilityCalculation> getVisibilityCalculations() {
        return ImmutableLabyrinthCollection.of(calculations);
    }

    /**
     * Get an exhaustive list that includes ALL command labels, even
     * those that represent aliases of the same command.
     *
     * @return all registered command labels
     */
    public static Set<String> getServerCommandListing() {
        return Collections.unmodifiableSet(commands.keySet());
    }

    /**
     * Get a Command object directly from the server command map by its label.
     *
     * @param label label of the command
     * @return Command object if found or null
     */
    public static @Nullable Command getCommandByLabel(@NotNull String label) {
        return commands.get(label);
    }

    /**
     * Get a sub command list for a given command by label.
     *
     * @param label label of the command.
     * @return Sub command list if found or null.
     */
    public static @Nullable SubCommandList getSubCommandList(@NotNull String label) {
        return wrappers.get(label);
    }

    /**
     * Read and or modify data from the servers command map possibly registering new commands into it.
     *
     * @param function The operation to run
     * @param <R> A possible return result.
     * @return A custom return value.
     */
    public static <R> R read(@NotNull Function<SimpleKeyedValue<CommandMap, Map<String, Command>>, R> function) {
        return function.apply(SimpleKeyedValue.of(commandMap, commands));
    }

    /**
     * Hide command visibility from specific players.
     *
     * @param calculation The command calculation to use.
     */
    public static void register(@NotNull CommandVisibilityCalculation calculation) {
        calculations.add(calculation);
    }

    /**
     * Register a command into the server command map.
     * 
     * @param command The command to be registered.
     */
    public static void register(@NotNull Command command) {
        Plugin plugin = JavaPlugin.getProvidingPlugin(command.getClass());
        read(entry -> entry.getKey().register(command.getLabel(), plugin.getName(), command));
    }

    /**
     * Inject custom sub commands into any plugin on the server!
     *
     * @param subCommand The sub command to inject.
     */
    public static void register(@NotNull SubCommand subCommand) {
        final Command parent = getCommandByLabel(subCommand.getCommand());
        if (parent != null) {
            final Plugin plugin = Optional.of((Plugin)JavaPlugin.getProvidingPlugin(parent.getClass())).orElseGet(() -> {
                if (parent instanceof PluginCommand) {
                    return ((PluginCommand)parent).getPlugin();
                } else return LabyrinthProvider.getInstance().getPluginInstance();
            });
            SubCommandList wrapper = wrappers.computeIfAbsent(subCommand.getCommand(), s -> {
                SubCommandList w = new SubCommandList(parent){
                    @Ordinal(24)
                    Command getCrossover() {
                        return this.parent;
                    }
                };
                Command wrapped = OrdinalProcedure.select(w, 24).cast(() -> Command.class);
                if (wrapped == null) throw new IllegalArgumentException("Cannot process non existent command base.");
                return read(entry -> {
                    Map<String, Command> commandMappings = entry.getValue();
                    CommandMap map = entry.getKey();
                    commandMappings.remove(parent.getName());
                    for (String alias : parent.getAliases()) {
                        if (commandMappings.containsKey(alias) && commandMappings.get(alias).getAliases().contains(alias)) {
                            commandMappings.remove(alias);
                        }
                    }
                    parent.unregister(map);
                    map.register(w.getCommand(), plugin.getName(), wrapped);
                    return w;
                });
            });
            if (!wrapper.contains(subCommand)) wrapper.add(subCommand);
        } else throw new IllegalArgumentException("Command " + subCommand.getCommand() + " either not found or not loaded yet.");
    }

    /**
     * Unregister a cached sub command.
     *
     * @param subCommand The sub command to remove
     */
    public static void unregister(@NotNull SubCommand subCommand) {
        final Command parent = getCommandByLabel(subCommand.getCommand());
        if (parent != null) {
            SubCommandList wrapper = wrappers.get(subCommand.getCommand());
            if (wrapper != null && wrapper.contains(subCommand)) wrapper.remove(subCommand);
        } else throw new IllegalArgumentException("Command " + subCommand.getCommand() + " either not found or not loaded yet.");
    }

    /**
     * Completely unregister a command from the server command map.
     *
     * @param command The command to remove.
     */
    public static void unregister(@NotNull Command command) {
        commands.remove(command.getName());
        for (String alias : command.getAliases()) {
            if (commands.containsKey(alias) && commands.get(alias).getAliases().contains(alias)) {
                commands.remove(alias);
            }

        }
        command.unregister(commandMap);
    }

}
