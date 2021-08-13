package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object for encapsulating data to {@link ItemStack} element and using it for a menu.
 *
 * @param <V> The data this item element holds.
 */
public class ItemElement<V> extends Menu.Element<ItemStack, Menu.Click> {

	private boolean slotted;

	private Navigation navigation;

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
	 * @param slot The slot this item belongs to.
	 * @return The same item element.
	 */
	public ItemElement<V> setSlot(int slot) {
		this.slot = slot;
		this.slotted = true;
		return this;
	}

	/**
	 * Setup a click event for when this item is interacted with.
	 *
	 * @param click The click event.
	 * @return The same item element.
	 */
	public ItemElement<V> setClick(Menu.Click click) {
		this.click = click;
		return this;
	}

	/**
	 * Set the itemstack for this element.
	 *
	 * @param item The item to use.
	 * @return The same item element.
	 */
	public ItemElement<V> setElement(ItemStack item) {
		this.item = new ItemStack(item);
		return this;
	}

	/**
	 * Set whether or not a player dragged this item into an inventory.
	 *
	 * @param playerAdded Whether or not this item was added by a player.
	 * @return The same item element.
	 */
	public ItemElement<V> setPlayerAdded(boolean playerAdded) {
		this.playerAdded = playerAdded;
		return this;
	}

	/**
	 * If an item is already applied make changes to the item or make a new item.
	 *
	 * @param edit The item edit procedure.
	 * @return The same item element.
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
	 * Set the navigation this item represents.
	 *
	 * @param navigation The navigation this item represents.
	 * @return The same item element.
	 */
	public ItemElement<V> setNavigation(Navigation navigation) {
		this.navigation = navigation;
		return this;
	}

	/**
	 * Get an optional containing possible data information.
	 *
	 * @return The given data for this item if present.
	 */
	public Optional<V> getData() {
		return Optional.ofNullable(this.data);
	}

	/**
	 * Get an optional containing possible item location information.
	 *
	 * @return The given slot for this item if present.
	 */
	public Optional<Integer> getSlot() {
		return slotted ? Optional.of(this.slot) : Optional.empty();
	}

	/**
	 * Get the navigation this item represents.
	 *
	 * @return The navigation this item represents or null.
	 */
	public @Nullable Navigation getNavigation() {
		return this.navigation;
	}

	/**
	 * Get the primary itemstack for this element.
	 *
	 * @return The itemstack for this element.
	 */
	@Override
	public @NotNull ItemStack getElement() {
		return this.item;
	}

	/**
	 * @return true if this item was dragged/dropped into the inventory by a player.
	 */
	public boolean isPlayerAdded() {
		return playerAdded;
	}

	/**
	 * @return true if this item has a slot location.
	 */
	public boolean isSlotted() {
		return slotted;
	}

	/**
	 * Set the inventory element this item belongs to.
	 *
	 * @param parent The element this item belongs to.
	 * @return The same item element.
	 */
	public ItemElement<V> setParent(@NotNull InventoryElement parent) {
		this.parent = parent;
		return this;
	}

	/**
	 * Gets the parent inventory.
	 *
	 * @return The element this item belongs to.
	 */
	public InventoryElement getParent() {
		return parent;
	}

	/**
	 * The click event for this item.
	 *
	 * @return The click operation to run.
	 */
	@Override
	public Menu.Click getAttachment() {
		return this.click;
	}

	/**
	 * Set the page this item belongs to.
	 *
	 * @param page The page
	 * @return The same item element.
	 */
	public ItemElement<V> setPage(@NotNull InventoryElement.Page page) {
		this.page = page;
		return this;
	}

	/**
	 * @return The page this item belongs to.
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
	 * Gets the item's display name if present, otherwise returning the items material type.
	 *
	 * @return The name of this item.
	 */
	public String getName() {
		return getElement().getItemMeta() != null && getElement().getItemMeta().hasDisplayName() ? getElement().getItemMeta().getDisplayName() : getElement().getType().name();
	}

	/**
	 * Designated navigation fields, used to tell the menu where to go next.
	 */
	public enum Navigation {
		Previous,
		Next,
		Back
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ItemElement)) return false;
		ItemElement<?> that = (ItemElement<?>) o;
		return isSlotted() == that.isSlotted() &&
				getSlot() == that.getSlot() &&
				isPlayerAdded() == that.isPlayerAdded() &&
				getNavigation() == that.getNavigation() &&
				Objects.equals(getData(), that.getData()) &&
				item.equals(that.item) &&
				Objects.equals(getPage(), that.getPage()) &&
				Objects.equals(click, that.click) &&
				getParent().equals(that.getParent());
	}

	@Override
	public int hashCode() {
		return Objects.hash(isSlotted(), getNavigation(), getSlot(), getData(), item, getPage(), click, getParent(), isPlayerAdded());
	}
}
