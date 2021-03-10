package com.github.sanctum.labyrinth.gui.builder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class PaginatedClose {

	private final InventoryView view;

	private final Player p;

	private final PaginatedBuilder builder;

	protected PaginatedClose(PaginatedBuilder builder, Player p, InventoryView view) {
		this.builder = builder;
		this.p = p;
		this.view = view;
	}

	public Player getPlayer() {
		return p;
	}

	public void clear() {
		HandlerList.unregisterAll(builder.getListener());
	}

	public void cancel() {
		builder.inv = Bukkit.createInventory(null, 54, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust(builder.page).getInventory());
	}

	public InventoryView getView() {
		return view;
	}
}
