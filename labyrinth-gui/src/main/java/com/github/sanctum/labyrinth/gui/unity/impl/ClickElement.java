package com.github.sanctum.labyrinth.gui.unity.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
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

	private final ClickType clickType;

	private final InventoryAction action;

	public ClickElement(Player clicker, int slot, InventoryAction action, ClickType type, ItemElement<?> element, InventoryView view) {
		super(clicker, view);
		this.action = action;
		this.clickType = type;
		this.slot = slot;
		this.parent = element;
	}

	/**
	 * @return The action taken on this click event.
	 */
	public InventoryAction getAction() {
		return action;
	}

	/**
	 * @return The type of click.
	 */
	public ClickType getClickType() {
		return clickType;
	}

	/**
	 * @return The event result prior to receiving the click
	 */
	public @Nullable Event.Result getResult() {
		return result;
	}

	/**
	 * Set the event result for this click.
	 *
	 * @param result The new event result.
	 */
	public void setResult(Event.Result result) {
		this.result = result;
	}

	/**
	 * Get's the slot clicked on.
	 *
	 * @return The slot clicked on.
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * @return true if clicking for this event is cancelled.
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Set the state of cancellation for this event.
	 *
	 * @param cancelled The state of cancellation.
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * @return true if hotbar buttons are allowed in this event.
	 */
	public boolean isHotbarAllowed() {
		return hotbarAllowed;
	}

	/**
	 * Set the state of cancellation for this events hotkey buttons.
	 *
	 * @param hotbarAllowed The state of cancellation
	 */
	public void setHotbarAllowed(boolean hotbarAllowed) {
		this.hotbarAllowed = hotbarAllowed;
	}

	/**
	 * @return The navigation event.
	 */
	public @Nullable Consumer getConsumer() {
		return playerConsumer;
	}

	/**
	 * If the parent item represents navigation use this to configure what happens on navigate
	 * success, Example -> {@link InventoryElement#open(Player)} to refresh the inventory for pagination.
	 *
	 * @param consumer The operation to run on navigate.
	 */
	public void setConsumer(Consumer consumer) {
		this.playerConsumer = consumer;
	}

	/**
	 * @return The parent item element involved with this click.
	 */
	public ItemElement<?> getParent() {
		return parent;
	}

	@FunctionalInterface
	public interface Consumer {

		void accept(Player target, boolean success);

	}

}
