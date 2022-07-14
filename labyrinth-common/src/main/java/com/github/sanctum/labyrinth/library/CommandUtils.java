package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.command.CommandVisibilityCalculation;
import com.github.sanctum.labyrinth.data.SimpleKeyedValue;
import com.github.sanctum.panther.container.ImmutablePantherCollection;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherSet;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Easily extract data from the server's internal command map.
 *
 * @author ms5984, Hempfest
 */
public final class CommandUtils {

	static Map<String, Command> commands;
	static CommandMap commandMap;
	private static final PantherCollection<CommandVisibilityCalculation> calculations = new PantherSet<>();

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
	 * @return An immutable collection of command visibility calculations.
	 */
	public static PantherCollection<CommandVisibilityCalculation> getVisibilityCalculations() {
		return ImmutablePantherCollection.of(calculations);
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
	 * Read and or modify data from the servers command map possibly registering new commands into it.
	 *
	 * @param function The operation to run
	 * @param <R>      A possible return result.
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
