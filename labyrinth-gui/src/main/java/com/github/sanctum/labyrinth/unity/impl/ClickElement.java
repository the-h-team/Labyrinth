package com.github.sanctum.labyrinth.unity.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Nullable;

public class ClickElement extends PlayerElement{

	private boolean cancelled;

	private final ItemElement<?> parent;

	private Consumer playerConsumer;

	private final int slot;

	public ClickElement(Player clicker, int slot, ItemElement<?> element, InventoryView view) {
		super(clicker, view);
		this.slot = slot;
		this.parent = element;
	}

	public int getSlot() {
		return slot;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public @Nullable Consumer getConsumer() {
		return playerConsumer;
	}

	public void setConsumer(Consumer consumer) {
		this.playerConsumer = consumer;
	}

	public ItemElement<?> getParent() {
		return parent;
	}

	@FunctionalInterface
	public interface Consumer {

		void accept(Player target, boolean success);

	}

}
