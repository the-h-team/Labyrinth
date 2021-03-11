package com.github.sanctum.labyrinth.gui.builder;

import java.util.function.Consumer;
import org.bukkit.inventory.ItemStack;

/**
 * An object used to specify custom logic to elements used from the provided collection in the menu builder.
 */
public class ProcessElement {

	private final PaginatedBuilder builder;

	private final SyncMenuItemPreProcessEvent event;

	protected ProcessElement(PaginatedBuilder builder, SyncMenuItemPreProcessEvent event) {
		this.builder = builder;
		this.event = event;
	}

	/**
	 * Create a lambda expression to formulate and customize primary menu elements.
	 *
	 * @param syncMenuItemPreProcessEventConsumer The item pre-process event.
	 */
	public void applyLogic(Consumer<SyncMenuItemPreProcessEvent> syncMenuItemPreProcessEventConsumer) {
		syncMenuItemPreProcessEventConsumer.accept(event);
	}

	/**
	 * Create and add any extra element additions and specify a click action for them.
	 *
	 * @param item The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public void invoke(ItemStack item, InventoryClick inventoryClick) {
		builder.actions.putIfAbsent(item, inventoryClick);
		builder.contents.add(item);
	}


}
