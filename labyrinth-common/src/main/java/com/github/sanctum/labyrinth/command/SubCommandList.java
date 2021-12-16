package com.github.sanctum.labyrinth.command;

import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollectionBase;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import com.github.sanctum.labyrinth.library.StringUtils;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class SubCommandList extends LabyrinthCollectionBase<SubCommand> {
	protected final Crossover parent;


	protected SubCommandList(@NotNull Command parent) {
		this.parent = new Crossover(parent);
	}

	public final String getCommand() {
		return parent.getLabel();
	}

	class Crossover extends Command {

		private final Command command;

		Crossover(Command pass) {
			super(pass.getName());
			this.command = pass;
			if (!this.command.getAliases().isEmpty()) {
				setAliases(this.command.getAliases());
			}
			if (this.command.getPermission() != null) setPermission(this.command.getPermission());
			if (!this.command.getDescription().isEmpty()) setDescription(this.command.getDescription());
			if (!this.command.getUsage().isEmpty()) setUsage(this.command.getUsage());
			if (this.command.getPermissionMessage() != null) setPermissionMessage(this.command.getPermissionMessage());
		}

		@NotNull
		@Override
		public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
			LabyrinthCollection<String> labels = new LabyrinthList<>();
			if (args.length > 0) {
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
