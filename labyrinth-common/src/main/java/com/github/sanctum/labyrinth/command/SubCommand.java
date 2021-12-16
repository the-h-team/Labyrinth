package com.github.sanctum.labyrinth.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class SubCommand {

	public abstract String getCommand();

	public abstract String getLabel();

	public abstract boolean player(Player p, String label, String[] args);

	public boolean console(CommandSender sender, String label, String[] args) {
		return false;
	}

	public List<String> tab(Player player, String alias, String[] args) {
		return new ArrayList<>();
	}

}
