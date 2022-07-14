package com.github.sanctum.labyrinth.command;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.interfacing.MessageInListener;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherCollectionBase;
import com.github.sanctum.panther.container.PantherList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class designed for easy bukkit sub command flow. Append or remove sub labels to registered commands.
 *
 * @author Hempfest
 */
public abstract class SubCommandList extends PantherCollectionBase<SubCommand> {
	protected final Crossover parent;


	protected SubCommandList(@NotNull Command parent) {
		this.parent = new Crossover(parent);
	}

	/**
	 * Get the main command label this sub command belongs to.
	 *
	 * @return The main command this sub command is for.
	 */
	public final @NotNull String getCommand() {
		return parent.getLabel();
	}

	/**
	 * Get a sub command from this list by its label.
	 *
	 * @param label The label of the sub command.
	 * @return a sub command match or null if not found.
	 */
	public final @Nullable SubCommand getSubCommand(@NotNull String label) {
		return stream().filter(s -> s.getLabel().equalsIgnoreCase(label)).findFirst().orElse(null);
	}

	/**
	 * Register a sub command into this list.
	 *
	 * @throws IllegalArgumentException if the subcommand provided doesn't belong to this command or if the command is not found or not yet loaded.
	 * @param subCommand The command to register.
	 */
	public final void register(@NotNull SubCommand subCommand) {
		if (subCommand.getCommand().equalsIgnoreCase(getCommand())) {
			// This should be a crossover object holding the original command.
			final Command parent = CommandUtils.getCommandByLabel(subCommand.getCommand());
			// Check if the provided sub command hasn't been wrapped yet.
			if (this.parent.command.equals(parent)) {
				final Plugin plugin = this.parent.getPlugin();
				CommandUtils.read(entry -> {
					Map<String, Command> commandMappings = entry.getValue();
					CommandMap map = entry.getKey();
					commandMappings.remove(parent.getName());
					for (String alias : parent.getAliases()) {
						if (commandMappings.containsKey(alias) && commandMappings.get(alias).getAliases().contains(alias)) {
							commandMappings.remove(alias);
						}
					}
					parent.unregister(map);
					map.register(getCommand(), plugin.getName(), this.parent);
					return this;
				});
				if (!contains(subCommand)) add(subCommand);
			} else throw new IllegalArgumentException("Command " + subCommand.getCommand() + " either not found, not loaded or mismatched.");
		}
	}

	/**
	 * Unregister a sub command from this list.
	 *
	 * @param subCommand The command to unregister.
	 */
	public final void unregister(@NotNull SubCommand subCommand) {
		if (subCommand.getCommand().equalsIgnoreCase(getCommand())) {
			final Command parent = CommandUtils.getCommandByLabel(subCommand.getCommand());
			if (this.parent.command.equals(parent)) {
				if (contains(subCommand)) remove(subCommand);
			} else throw new IllegalArgumentException("Command " + subCommand.getCommand() + " either not found or not loaded yet.");
		}
	}

	class Crossover extends Command {

		private final Command command;
		private final Plugin plugin;

		Crossover(Command pass) {
			super(pass.getName());
			this.command = pass;
			this.plugin = Optional.of((Plugin)JavaPlugin.getProvidingPlugin(pass.getClass())).orElseGet(() -> {
				if (pass instanceof PluginCommand) {
					return ((PluginCommand)pass).getPlugin();
				} else return LabyrinthProvider.getInstance().getPluginInstance();
			});
			if (!this.command.getAliases().isEmpty()) {
				setAliases(this.command.getAliases());
			}
			if (this.command.getPermission() != null) setPermission(this.command.getPermission());
			if (!this.command.getDescription().isEmpty()) setDescription(this.command.getDescription());
			if (!this.command.getUsage().isEmpty()) setUsage(this.command.getUsage());
			if (this.command.getPermissionMessage() != null) setPermissionMessage(this.command.getPermissionMessage());
		}

		public Plugin getPlugin() {
			return plugin;
		}

		@NotNull
		@Override
		public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
			PantherCollection<String> labels = new PantherList<>();
			if (args.length > 0) {
				if (SubCommandList.this instanceof MessageInListener) {
					((MessageInListener)SubCommandList.this).onReceiveSuggestion(String.join(" ", args));
				}
				for (SubCommand sub : SubCommandList.this) {
					if (args.length == 1) {
						Stream.of(sub.getLabel()).filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).forEach(labels::add);
					}
					if (args[0].equalsIgnoreCase(sub.getLabel())) {
						List<String> t = new LinkedList<>(Arrays.asList(args));
						t.removeIf(s -> StringUtils.use(s).containsIgnoreCase(sub.getLabel()));
						return sub.tab((Player) sender, alias, t.toArray(new String[0]));
					}
				}
			}
			labels.addAll(this.command.tabComplete(sender, alias, args));
			return labels.stream().collect(Collectors.toList());
		}

		@Override
		public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
			if (args.length > 0) {
				if (SubCommandList.this instanceof MessageInListener) {
					((MessageInListener)SubCommandList.this).onReceiveMessage(String.join(" ", args));
				}
				for (SubCommand sub : SubCommandList.this) {
					if (args[0].equalsIgnoreCase(sub.getLabel())) {
						List<String> t = new LinkedList<>(Arrays.asList(args));
						t.removeIf(s -> StringUtils.use(s).containsIgnoreCase(sub.getLabel()));
						String[] realArgs = t.toArray(new String[0]);
						if (sender instanceof Player) {
							return sub.player((Player) sender, commandLabel, realArgs);
						} else {
							return sub.console(sender, commandLabel, realArgs);
						}
					}
				}
			}
			return this.command.execute(sender, commandLabel, args);
		}


	}
}
