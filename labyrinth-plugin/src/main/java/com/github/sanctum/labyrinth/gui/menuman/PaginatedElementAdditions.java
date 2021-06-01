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
	 * @param item           The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public PaginatedElementAdditions<T> invoke(ItemStack item, PaginatedMenuClick<T> inventoryClick) {
		builder.ITEM_ACTIONS.putIfAbsent(item, inventoryClick);
		builder.PROCESS_LIST.add(item);
		builder.INITIAL_CONTENTS.putIfAbsent(item, -1);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action and slot for them.
	 *
	 * @param item           The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public PaginatedElementAdditions<T> invoke(ItemStack item, int slot, PaginatedMenuClick<T> inventoryClick) {
		builder.ITEM_ACTIONS.putIfAbsent(item, inventoryClick);
		builder.PROCESS_LIST.add(item);
		builder.INITIAL_CONTENTS.putIfAbsent(item, slot);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action for them.
	 *
	 * @param supplier       The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public PaginatedElementAdditions<T> invoke(Supplier<ItemStack> supplier, PaginatedMenuClick<T> inventoryClick) {
		ItemStack item = supplier.get();
		builder.ITEM_ACTIONS.putIfAbsent(item, inventoryClick);
		builder.PROCESS_LIST.add(item);
		builder.INITIAL_CONTENTS.putIfAbsent(item, -1);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action and slot for them.
	 *
	 * @param supplier       The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public PaginatedElementAdditions<T> invoke(Supplier<ItemStack> supplier, int slot, PaginatedMenuClick<T> inventoryClick) {
		ItemStack item = supplier.get();
		builder.ITEM_ACTIONS.putIfAbsent(item, inventoryClick);
		builder.PROCESS_LIST.add(item);
		builder.INITIAL_CONTENTS.putIfAbsent(item, slot);
		return this;
	}

	public PaginatedBuilder<T> add() {
		return builder;
	}

}
