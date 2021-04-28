package com.github.sanctum.labyrinth.gui.builder;

import java.util.UUID;
import java.util.function.Supplier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * An object used to specify custom logic to elements used from the provided collection in the menu builder.
 */
public class ProcessElement {

	private final PaginatedBuilder builder;
	private ItemStack item;
	private final String context;

	protected ProcessElement(PaginatedBuilder builder, ItemStack item, String context) {
		this.context = context;
		this.item = item;
		this.builder = builder;
	}

	/**
	 * The item involved.
	 *
	 * @return The ItemStack being built.
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * The unique ID from the menu.
	 *
	 * @return The menus unique ID.
	 */
	public UUID getId() {
		return builder.getId();
	}

	/**
	 * Assign a click action to the item being built.
	 *
	 * @return An action builder for an item.
	 */
	public ActionBuilder action() {
		return new ActionBuilder(item, builder);
	}

	/**
	 * The context to be attached to the item from the collection.
	 *
	 * @return context from the menus collection.
	 */
	public String getContext() {
		return context;
	}

	/**
	 * Build an item using only a name.
	 * *WARNING*: Only for string UUID use.
	 * <p>
	 * Converts the provided string context from the menu collection back into a player ID
	 * and is only to be used if the collection contains player UUID's in string form.
	 *
	 * @param displayName The username to apply to the itemstack.
	 */
	public void craftItem(String displayName) {
		ItemMeta meta = item.getItemMeta();
		UUID id = UUID.fromString(getContext());
		meta.setDisplayName(displayName);
		meta.getPersistentDataContainer().set(builder.getKey(), PersistentDataType.STRING, getContext());
		item.setItemMeta(meta);
	}

	/**
	 * Completely customize and build the item used from the collection and design
	 * how it will be used.
	 *
	 * @param itemStackSupplier A lambda expression initializing the item format to be used.
	 */
	public void buildItem(Supplier<ItemStack> itemStackSupplier) {
		this.item = itemStackSupplier.get();
	}

}
