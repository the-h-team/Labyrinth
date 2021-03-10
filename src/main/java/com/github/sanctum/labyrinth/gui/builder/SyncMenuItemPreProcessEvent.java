package com.github.sanctum.labyrinth.gui.builder;

import java.util.UUID;
import java.util.function.Supplier;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SyncMenuItemPreProcessEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final String context;

	private ItemStack item;

	private final PaginatedBuilder builder;

	public SyncMenuItemPreProcessEvent(PaginatedBuilder builder, String context, ItemStack item) {
		this.builder = builder;
		this.context = context;
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

	public ActionBuilder action() {
		return new ActionBuilder(item, builder);
	}

	public String getContext() {
		return context;
	}

	public void craftItem(String displayName) {
		ItemMeta meta = item.getItemMeta();
		UUID id = UUID.fromString(getContext());
		meta.setDisplayName(displayName);
		meta.getPersistentDataContainer().set(builder.getKey(), PersistentDataType.STRING, getContext());
		item.setItemMeta(meta);
	}

	public void buildItem(Supplier<ItemStack> itemStackSupplier) {
		this.item = itemStackSupplier.get();
	}
}
