package com.github.sanctum.labyrinth.test;

import com.github.sanctum.labyrinth.formatting.TabCompletion;
import com.github.sanctum.labyrinth.gui.shared.SharedMenu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class CommandTest extends Command {


	public CommandTest() {
		super("fart");
	}


	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		return TabCompletion.build(getName(), args)
				.level(1)
				.completeAnywhere(getName())
				.filter(() -> {
					List<String> toAdd = new ArrayList<>();
					if (sender.isOp()) {
						toAdd.add("On");
					}
					toAdd.add("Arrow");
					return toAdd;
				})
				.collect()
				.level(2)
				.require("On", 0)
				.filter(() -> new ArrayList<>(Collections.singletonList("You")))
				.completeAt(1)
				.collect()
				.level(2)
				.require("Arrow", 0)
				.filter(() -> new ArrayList<>(Collections.singletonList("in")))
				.completeAt(1)
				.collect()
				.level(3)
				.require("in", 1)
				.filter(() -> new ArrayList<>(Collections.singletonList("knee")))
				.completeAt(2)
				.collect()
				.get(args.length);
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

		if (!(sender instanceof Player))
			return true;

		Player p = (Player) sender;

		if (args.length == 0) {
			SharedMenu menu = SharedMenu.get(4208);
			menu.setItem(0, () -> {
				ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("Testt");
				item.setItemMeta(meta);
				return item;
			}, click -> {
				click.getWhoClicked().closeInventory();
				click.cancel();
			});
			p.openInventory(menu.getInventory());
			return true;
		}

		if (args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			p.openInventory(SharedMenu.open(target));
			return true;
		}

		return true;
	}
}
