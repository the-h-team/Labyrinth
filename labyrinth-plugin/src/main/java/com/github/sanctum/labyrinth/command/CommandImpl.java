package com.github.sanctum.labyrinth.command;

import com.github.sanctum.labyrinth.formatting.TabCompletion;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

final class CommandImpl extends Command {

	private final CommandBuilder builder;

	protected CommandImpl(CommandBuilder builder) {
		super(builder.label);
		this.builder = builder;
		if (builder.description != null) {
			setDescription(builder.description);
		}
		if (builder.usage != null) {
			setUsage(builder.usage);
		}
		if (builder.permission != null) {
			setPermission(builder.permission);
		}
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {

		if (builder.playerResultingCompleter != null) {
			return builder.playerResultingCompleter.run((Player) sender, args, TabCompletion.build(builder.label).forArgs(args)).get(args.length);
		}

		return super.tabComplete(sender, alias, args);
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

		if (!(sender instanceof Player)) {
			if (builder.consoleResultingExecutor != null) {
				return builder.consoleResultingExecutor.run(sender, commandLabel, args);
			}
		} else {
			if (builder.playerResultingExecutor != null) {
				return builder.playerResultingExecutor.run((Player) sender, commandLabel, args);
			}
			return true;
		}

		return true;
	}
}
