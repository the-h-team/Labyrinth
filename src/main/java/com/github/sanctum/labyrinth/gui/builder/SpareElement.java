package com.github.sanctum.labyrinth.gui.builder;

import java.util.function.Supplier;
import org.bukkit.inventory.ItemStack;

/**
 * An object used to add additional items with specified properties to a {@link PaginatedBuilder}
 */
public class SpareElement {

	private final PaginatedBuilder builder;

	protected SpareElement(PaginatedBuilder paginatedBuilder) {
		this.builder = paginatedBuilder;
	}

	/**
	 * Create and add any extra element additions and specify a click action for them.
	 *
	 * @param item The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public SpareElement invoke(ItemStack item, InventoryClick inventoryClick) {
		builder.actions.putIfAbsent(item, inventoryClick);
		builder.contents.add(item);
		builder.inv.addItem(item);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action and slot for them.
	 *
	 * @param item The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public SpareElement invoke(ItemStack item, int slot, InventoryClick inventoryClick) {
		builder.actions.putIfAbsent(item, inventoryClick);
		builder.contents.add(item);
		builder.inv.setItem(slot, item);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action for them.
	 *
	 * @param supplier The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public SpareElement invoke(Supplier<ItemStack> supplier, InventoryClick inventoryClick) {
		ItemStack item = supplier.get();
		builder.actions.putIfAbsent(item, inventoryClick);
		builder.contents.add(item);
		builder.inv.addItem(item);
		return this;
	}

	/**
	 * Create and add any extra element additions and specify a click action and slot for them.
	 *
	 * @param supplier The item to add.
	 * @param inventoryClick The action to be ran upon item being clicked.
	 */
	public SpareElement invoke(Supplier<ItemStack> supplier, int slot, InventoryClick inventoryClick) {
		ItemStack item = supplier.get();
		builder.actions.putIfAbsent(item, inventoryClick);
		builder.contents.add(item);
		builder.inv.setItem(slot, item);
		return this;
	}

	public PaginatedBuilder add() {
		return builder;
	}

}
