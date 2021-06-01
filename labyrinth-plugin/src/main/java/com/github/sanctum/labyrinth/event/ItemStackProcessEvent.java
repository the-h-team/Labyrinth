package com.github.sanctum.labyrinth.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemStackProcessEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private ItemStack item;

	private final String name;

	public ItemStackProcessEvent(String name, ItemStack item) {
		this.item = item;
		this.name = name;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public String getName() {
		return name;
	}

	public ItemStack getItem() {
		return item;
	}

	public void updateItem(ItemStack newItem) {
		this.item = newItem;
	}

}
