package com.github.sanctum.labyrinth.unity.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Nullable;

public class ClickElement extends PlayerElement{

	private boolean cancelled;

	private boolean hotbarAllowed;

	private final ItemElement<?> parent;

	private Consumer playerConsumer;

	private Event.Result result;

	private final int slot;

	private final InventoryAction action;

	public ClickElement(Player clicker, int slot, InventoryAction action, ItemElement<?> element, InventoryView view) {
		super(clicker, view);
		this.action = action;
		this.slot = slot;
		this.parent = element;
	}

	public InventoryAction getAction() {
		return action;
	}

	public @Nullable Event.Result getResult() {
		return result;
	}

	public void setResult(Event.Result result) {
		this.result = result;
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

	public boolean isHotbarAllowed() {
		return hotbarAllowed;
	}

	public void setHotbarAllowed(boolean hotbarAllowed) {
		this.hotbarAllowed = hotbarAllowed;
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
