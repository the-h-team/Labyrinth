package com.github.sanctum.labyrinth.gui.builder;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class PaginatedMenu {

	private final PaginatedBuilder builder;

	protected PaginatedMenu(PaginatedBuilder builder) {
		this.builder = builder;
	}

	public void open(Player p) {
		builder.inv = Bukkit.createInventory(null, 54, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust().getInventory());
	}

	public void open(Player p, int page) {
		builder.inv = Bukkit.createInventory(null, 54, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust(page).getInventory());
	}

	public void unregister() {
		HandlerList.unregisterAll(builder.getListener());
	}

	public UUID getId() {
		return builder.getId();
	}

}
