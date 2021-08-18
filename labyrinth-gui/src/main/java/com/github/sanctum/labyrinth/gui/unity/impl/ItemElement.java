package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object for encapsulating data to {@link ItemStack} element and using it for a menu.
 *
 * @param <V> the data type this item element holds
 */
public class ItemElement<V> extends Menu.Element<ItemStack, Menu.Click> {

	private boolean slotted;

	private ControlType type = ControlType.ITEM;

	private int slot;

	private final V data;

	private ItemStack item;

	private InventoryElement.Page page;

	private Menu.Click click;

	private InventoryElement parent;

	private boolean playerAdded;

	public ItemElement() {
		this.data = null;
	}

	public ItemElement(V data) {
		this.data = data;
	}

	/**
	 * Set the inventory slot for this item element.
	 *
	 * @param slot the slot this item belongs to
	 * @return this item element
	 */
	public ItemElement<V> setSlot(int slot) {
		this.slot = slot;
		this.slotted = true;
		return this;
	}

	/**
	 * Setup a click event for when this item is interacted with.
	 *
	 * @param click the click event
	 * @return this item element
	 */
	public ItemElement<V> setClick(Menu.Click click) {
		this.click = click;
		return this;
	}

	/**
	 * Set the itemstack for this element.
	 *
	 * @param item the item to use
	 * @return this item element
	 */
	public ItemElement<V> setElement(ItemStack item) {
		this.item = new ItemStack(item);
		return this;
	}

	/**
	 * Set whether or not a player dragged this item into an inventory.
	 *
	 * @param playerAdded Whether or not this item was added by a player.
	 * @return this item element
	 */
	public ItemElement<V> setPlayerAdded(boolean playerAdded) {
		this.playerAdded = playerAdded;
		return this;
	}

	/**
	 * If an item is already applied make changes to the item or make a new item.
	 *
	 * @param edit the item edit procedure
	 * @return this item element
	 */
	public ItemElement<V> setElement(Function<Item.Edit, ItemStack> edit) {
		Item.Edit ed;
		if (this.item != null) {
			ed = new Item.Edit(this.item);
		} else {
			ed = new Item.Edit(Material.DIRT);
		}
		return setElement(edit.apply(ed));
	}

	/**
	 * Set the type this item represents.
	 *
	 * @param type the type this item represents
	 * @return this item element
	 */
	public ItemElement<V> setType(ControlType type) {
		this.type = type;
		return this;
	}

	/**
	 * Get an optional containing possible data information.
	 *
	 * @return the given data for this item if present
	 */
	public Optional<V> getData() {
		return Optional.ofNullable(this.data);
	}

	/**
	 * Get an optional containing possible item location information.
	 *
	 * @return the given slot for this item if present
	 */
	public Optional<Integer> getSlot() {
		return slotted ? Optional.of(this.slot) : Optional.empty();
	}

	/**
	 * Get the navigation this item represents.
	 *
	 * @return the navigation this item represents or null
	 */
	public @Nullable ItemElement.ControlType getType() {
		return this.type;
	}

	/**
	 * Get the primary itemstack for this element.
	 *
	 * @return the itemstack for this element
	 */
	@Override
	public @NotNull ItemStack getElement() {
		return this.item;
	}

	/**
	 * Check if this item was dragged/dropped into the inventory by a player.
	 *
	 * @return true if this item was added by a player
	 */
	public boolean isPlayerAdded() {
		return playerAdded;
	}

	/**
	 * Check if this item has a slot location.
	 *
	 * @return true if this item has a slot location
	 */
	public boolean isSlotted() {
		return slotted;
	}

	/**
	 * Set the inventory element this item belongs to.
	 *
	 * @param parent the element this item will belong to
	 * @return this item element
	 */
	public ItemElement<V> setParent(@NotNull InventoryElement parent) {
		this.parent = parent;
		return this;
	}

	/**
	 * Get the parent inventory.
	 *
	 * @return the element this item belongs to
	 */
	public InventoryElement getParent() {
		return parent;
	}

	/**
	 * The click event for this item.
	 *
	 * @return the click operation to run
	 */
	@Override
	public Menu.Click getAttachment() {
		return this.click;
	}

	/**
	 * Set the page this item belongs to.
	 *
	 * @param page the page
	 * @return this item element
	 */
	public ItemElement<V> setPage(@NotNull InventoryElement.Page page) {
		this.page = page;
		return this;
	}

	/**
	 * Get the page this item belongs to.
	 *
	 * @return the page this item belongs to
	 */
	public InventoryElement.Page getPage() {
		if (this.page == null) {
			return getParent().getPage(1);
		}
		return this.page;
	}

	/**
	 * Remove this item from cache.
	 *
	 * @param sincere whether to delete from actual inventory or not
	 */
	public final void remove(boolean sincere) {
		getParent().removeItem(this, sincere);
	}

	/**
	 * Remove this item from player cache.
	 *
	 * @param player the player
	 * @param sincere whether to delete from actual inventory or not
	 */
	public final void remove(Player player, boolean sincere) {
		getParent().removeItem(player, this, sincere);
	}

	/**
	 * Gets the item's display name if present, otherwise returning the items material type.
	 *
	 * @return the name of this item
	 */
	public String getName() {
		return getElement().getItemMeta() != null && getElement().getItemMeta().hasDisplayName() ? getElement().getItemMeta().getDisplayName() : getElement().getType().name();
	}

	/**
	 * Designated navigation fields, used to tell the menu what this item represents.
	 */
	public enum ControlType {

		/**
		 * Symbolizes an entry point backwards.
		 */
		BUTTON_BACK,

		/**
		 * Symbolizes an entry point forward.
		 */
		BUTTON_NEXT,

		/**
		 * Symbolizes an entry point exit.
		 */
		BUTTON_EXIT,

		/**
		 * Symbolizes a normal item.
		 */
		ITEM,

		/**
		 * Symbolizes a {@link BorderElement} item.
		 */
		ITEM_BORDER,

		/**
		 * Symbolizes a {} item.
		 */
		ITEM_FILLER

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ItemElement)) return false;
		ItemElement<?> that = (ItemElement<?>) o;
		return isSlotted() == that.isSlotted() &&
				getSlot() == that.getSlot() &&
				isPlayerAdded() == that.isPlayerAdded() &&
				getType() == that.getType() &&
				Objects.equals(getData(), that.getData()) &&
				item.equals(that.item) &&
				Objects.equals(getPage(), that.getPage()) &&
				Objects.equals(click, that.click) &&
				getParent().equals(that.getParent());
	}

	@Override
	public int hashCode() {
		return Objects.hash(isSlotted(), getType(), getSlot(), getData(), item, getPage(), click, getParent(), isPlayerAdded());
	}
}
