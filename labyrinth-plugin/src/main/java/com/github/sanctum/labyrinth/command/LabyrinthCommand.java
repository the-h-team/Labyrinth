package com.github.sanctum.labyrinth.command;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.formatting.completion.SimpleTabCompletion;
import com.github.sanctum.labyrinth.formatting.completion.TabCompletionIndex;
import com.github.sanctum.labyrinth.formatting.string.BlockChar;
import com.github.sanctum.labyrinth.formatting.string.ImageBreakdown;
import com.github.sanctum.labyrinth.formatting.string.MessageBreakdown;
import com.github.sanctum.labyrinth.library.Mailer;
import com.github.sanctum.panther.placeholder.Placeholder;
import com.github.sanctum.panther.placeholder.PlaceholderRegistration;
import com.github.sanctum.panther.util.TypeAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class LabyrinthCommand extends Command {

	Labyrinth labyrinth;
	public LabyrinthCommand(@NotNull LabyrinthCommandToken token) throws IllegalAccessException {
		super("labyrinth");
		if (!token.isValid()) throw new IllegalAccessException("Invalid command token! Not permitted!");
		this.labyrinth = token.get();
	}

	private final SimpleTabCompletion completion = SimpleTabCompletion.empty();
	private final TypeAdapter<Player> conversion = () -> Player.class;

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		Mailer mailer = Mailer.empty(sender).prefix().start("&2Labyrinth").middle(":").finish();
		if (!sender.isOp() && !sender.hasPermission("labyrinth")) {
			mailer.chat("&cYou don't have access to labyrinth functions.").deploy();
			return true;
		}
		if (args.length == 0) {
			mailer.chat("&6Currently running version &r" + labyrinth.getDescription().getVersion()).queue();
			return true;
		}

		if (args.length == 1) {
			String label = args[0];
			if (label.equalsIgnoreCase("version")) {
				mailer.chat("&6Currently running version &r" + labyrinth.getDescription().getVersion()).queue();
				return true;
			}
			if (label.equalsIgnoreCase("placeholder")) {
				mailer.chat("&cInvalid usage: &6/" + commandLabel + " " + label + " <placeholder> | &8[playerName]").queue();
				return true;
			}
			return true;
		}

		if (args.length == 2) {
			String label = args[0];
			String argument = args[1];

			if (label.equalsIgnoreCase("paint")) {
				ImageBreakdown breakdown = new MessageBreakdown(argument, 24, 24, BlockChar.SOLID);
				String[] s = breakdown.read();
				for (String st : s) {
					mailer.chat(st).deploy();
				}
				return true;
			}
			if (label.equalsIgnoreCase("placeholder")) {
				if (sender instanceof Player) {
					sender.sendMessage(PlaceholderRegistration.getInstance().replaceAll(argument, conversion.cast(sender)));
				} else {
					sender.sendMessage(PlaceholderRegistration.getInstance().replaceAll(argument, sender));
				}
				return true;
			}
			return true;
		}

		if (args.length == 3) {
			String label = args[0];
			String argument = args[1];

			if (label.equalsIgnoreCase("placeholder")) {
				if (sender instanceof Player) {
					sender.sendMessage(PlaceholderRegistration.getInstance().replaceAll(argument, conversion.cast(sender)));
				} else {
					sender.sendMessage(PlaceholderRegistration.getInstance().replaceAll(argument, sender));
				}
				return true;
			}
			return true;
		}

		return false;
	}

	@NotNull
	@Override
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		completion.fillArgs(args);
		completion.then(TabCompletionIndex.ONE, "placeholder", "version", "paint");
		List<String> placeholders = new ArrayList<>();
		PlaceholderRegistration.getInstance().getHistory().entries().stream().sorted(Comparator.comparing(e -> e.getKey().get())).forEach(e -> {
			for (Placeholder placeholder : e.getValue()) {
				String result = placeholder.toRaw();
				placeholders.add(placeholder.start() + e.getKey().get() + result.substring(1));
			}
		});
		completion.then(TabCompletionIndex.TWO, "placeholder", TabCompletionIndex.ONE, placeholders);
		completion.then(TabCompletionIndex.THREE, "placeholder", TabCompletionIndex.ONE, Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList()));

		return completion.get();
	}

}
