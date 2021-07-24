package com.github.sanctum.labyrinth.gui.menuman;

import java.util.UUID;
import java.util.function.Supplier;
import org.bukkit.inventory.ItemStack;

/**
 * An object used to specify custom logic to elements used from the provided collection in the menu builder.
 */
public class PaginatedProcessAction<T> {

	private final PaginatedBuilder<T> builder;
	private ItemStack item;
	private final T t;

	protected PaginatedProcessAction(PaginatedBuilder<T> builder, T t) {
		this.t = t;
		this.builder = builder;
	}

	/**
	 * The unique ID from the menu.
	 *
	 * @return the unique ID from the menu
	 */
	public UUID getId() {
		return builder.getId();
	}

	/**
	 * Apply what happens when the item is clicked on.
	 * <p>
	 * <strong>All data provided will be applied on RUNTIME</strong>
	 *
	 * @param click the lambda/method reference to apply
	 * @return this action builder
	 */
	public PaginatedProcessAction<T> setClick(PaginatedMenuClick<T> click) {
		if (item == null) {
			throw new IllegalStateException("Missing item construction, ensure that happens before action click processing.");
		}
		builder.itemActions.putIfAbsent(item, click);
		return this;
	}

	/**
	 * Supply the item construction for his process.
	 *
	 * @param itemStackSupplier the item construction to take place while processing the menu
	 * @return this action builder
	 */
	public PaginatedProcessAction<T> setItem(Supplier<ItemStack> itemStackSupplier) {
		this.item = itemStackSupplier.get();
		return this;
	}

	/**
	 * @return The item being used for processing;
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * The context to be attached to the item from the collection.
	 *
	 * @return context from the menus collection
	 */
	public T getContext() {
		return t;
	}

}
