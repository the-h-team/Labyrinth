package com.github.sanctum.labyrinth.gui.menuman;

import java.util.function.Supplier;
import org.bukkit.inventory.ItemStack;

/**
 * An object used to add additional items with specified properties to a {@link PaginatedBuilder}
 */
public class PaginatedElementAdditions<T> {

	private final PaginatedBuilder<T> builder;

	protected PaginatedElementAdditions(PaginatedBuilder<T> paginatedBuilder) {
		this.builder = paginatedBuilder;
	}

	/**
	 * Create and add any extra element additions and specify a click action for them.
	 *
	 * @param item           the item to add
	 * @param inventoryClick the action to be run upon item being clicked
	 */
	public PaginatedElementAdditions<T> invoke(ItemStack item, PaginatedMenuClick<T> inventoryClick) {
		builder.itemActions.putIfAbsent(item, inventoryClick);
		builder.processList.add(item);
		builder.initialContents.putIfAbsent(item, -1);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action and slot for them.
	 *
	 * @param item           the item to add
	 * @param slot           the desired slot
	 * @param inventoryClick the action to be run upon item being clicked
	 */
	public PaginatedElementAdditions<T> invoke(ItemStack item, int slot, PaginatedMenuClick<T> inventoryClick) {
		builder.itemActions.putIfAbsent(item, inventoryClick);
		builder.processList.add(item);
		builder.initialContents.putIfAbsent(item, slot);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action for them.
	 *
	 * @param supplier       the item to add
	 * @param inventoryClick the action to be run upon item being clicked
	 */
	public PaginatedElementAdditions<T> invoke(Supplier<ItemStack> supplier, PaginatedMenuClick<T> inventoryClick) {
		ItemStack item = supplier.get();
		builder.itemActions.putIfAbsent(item, inventoryClick);
		builder.processList.add(item);
		builder.initialContents.putIfAbsent(item, -1);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action and slot for them.
	 *
	 * @param supplier       the item to add
	 * @param slot           the desired slot
	 * @param inventoryClick the action to be run upon item being clicked
	 */
	public PaginatedElementAdditions<T> invoke(Supplier<ItemStack> supplier, int slot, PaginatedMenuClick<T> inventoryClick) {
		ItemStack item = supplier.get();
		builder.itemActions.putIfAbsent(item, inventoryClick);
		builder.processList.add(item);
		builder.initialContents.putIfAbsent(item, slot);
		return this;
	}

	public PaginatedBuilder<T> add() {
		return builder;
	}

}
