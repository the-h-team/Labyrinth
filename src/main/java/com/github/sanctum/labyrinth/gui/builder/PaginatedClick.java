package com.github.sanctum.labyrinth.gui.builder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class PaginatedClick {

	private final InventoryView view;

	private final Player p;

	private final ItemStack clickedItem;

	private final PaginatedBuilder builder;

	protected PaginatedClick(PaginatedBuilder builder, Player p, InventoryView view, ItemStack item) {
		this.builder = builder;
		this.p = p;
		this.view = view;
		this.clickedItem = item;
	}

	public ItemStack getClickedItem() {
		return clickedItem;
	}

	public Player getPlayer() {
		return p;
	}

	public void refresh() {
		builder.inv = Bukkit.createInventory(null, 54, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		p.openInventory(builder.adjust().getInventory());
	}

	public InventoryView getView() {
		return view;
	}
}
