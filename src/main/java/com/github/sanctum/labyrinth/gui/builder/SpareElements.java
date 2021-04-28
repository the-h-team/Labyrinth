package com.github.sanctum.labyrinth.gui.builder;

import java.util.function.Supplier;
import org.bukkit.inventory.ItemStack;

/**
 * An object used to add additional items with specified properties to a {@link PaginatedBuilder}
 */
public class SpareElements {

	private final PaginatedBuilder builder;

	protected SpareElements(PaginatedBuilder paginatedBuilder) {
		this.builder = paginatedBuilder;
	}

	/**
	 * Create and add any extra element additions and specify a click action for them.
	 *
	 * @param item           The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public SpareElements invoke(ItemStack item, InventoryClick inventoryClick) {
		builder.actions.putIfAbsent(item, inventoryClick);
		builder.contents.add(item);
		builder.additional.putIfAbsent(item, -1);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action and slot for them.
	 *
	 * @param item           The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public SpareElements invoke(ItemStack item, int slot, InventoryClick inventoryClick) {
		builder.actions.putIfAbsent(item, inventoryClick);
		builder.contents.add(item);
		builder.additional.putIfAbsent(item, slot);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action for them.
	 *
	 * @param supplier       The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public SpareElements invoke(Supplier<ItemStack> supplier, InventoryClick inventoryClick) {
		ItemStack item = supplier.get();
		builder.actions.putIfAbsent(item, inventoryClick);
		builder.contents.add(item);
		builder.additional.putIfAbsent(item, -1);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action and slot for them.
	 *
	 * @param supplier       The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public SpareElements invoke(Supplier<ItemStack> supplier, int slot, InventoryClick inventoryClick) {
		ItemStack item = supplier.get();
		builder.actions.putIfAbsent(item, inventoryClick);
		builder.contents.add(item);
		builder.additional.putIfAbsent(item, slot);
		return this;
	}

	public PaginatedBuilder add() {
		return builder;
	}

}
