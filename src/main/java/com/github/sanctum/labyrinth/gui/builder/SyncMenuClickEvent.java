package com.github.sanctum.labyrinth.gui.builder;

import java.util.UUID;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SyncMenuClickEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final ItemStack item;

	private final InventoryView view;

	private final PaginatedBuilder builder;

	private final Player whoClicked;

	private boolean cancelled;

	public SyncMenuClickEvent(PaginatedBuilder builder, Player whoClicked, InventoryView view, ItemStack item) {
		this.builder = builder;
		this.whoClicked = whoClicked;
		this.view = view;
		this.item = item;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public ItemStack getItem() {
		return item;
	}

	public UUID getId() {
		return builder.getId();
	}

	public Player getWhoClicked() {
		return whoClicked;
	}

	public InventoryView getView() {
		return view;
	}

	public void refresh() {
		builder.inv = Bukkit.createInventory(null, 54, builder.title.replace("{PAGE}", "" + (builder.page + 1)).replace("{MAX}", "" + builder.getMaxPages()));
		whoClicked.openInventory(builder.adjust(builder.page).getInventory());
	}

	public void close() {
		whoClicked.closeInventory();
		HandlerList.unregisterAll(builder.listener);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
