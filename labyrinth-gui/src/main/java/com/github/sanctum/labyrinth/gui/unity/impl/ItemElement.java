package com.github.sanctum.labyrinth.gui.unity.impl;

import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
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

	private ControlType type;

	private int slot;

	private final V data;

	private ItemStack item;

	private InventoryElement.Page page;

	private Consumer<InventoryElement> clickGenerator;

	private Menu.Click click;

	private InventoryElement parent;

	private boolean playerAdded;

	public ItemElement() {
		this(null);
	}

	public ItemElement(V data) {
		this.data = data;
		setType(ControlType.DISPLAY);
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
		this.type = ControlType.CUSTOM;
		this.click = click;
		this.clickGenerator = e -> this.click = click;
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
		this.clickGenerator = e -> type.generateAndSetClick(e, this);
		if (parent != null) {
			clickGenerator.accept(parent);
		}
		type.generateAndSetClick(parent, this);
		return this;
	}

	/**
	 * Generates a custom click out of the standard behaviour of the passed type and the given click.
	 * Behaviour of the passed custom click may override the behaviour of the passed control type.
	 *
	 * @param type  the control type which provides the template
	 * @param click the custom click action to be added to the template
	 * @return this item element
	 */
	public ItemElement<V> setTypeAndAddAction(ControlType type, Menu.Click click) {
		this.type = type;
		this.clickGenerator = e -> {
			Menu.Click template = type.clickHandlerGenerator.apply(e);
			if (template == null) {
				this.click = click;
			} else {
				this.click = c -> {
					template.apply(c);
					click.apply(c);
				};
			}
		};
		if (parent != null) {
			clickGenerator.accept(parent);
		}
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
	 * @return the control type this item represents or null
	 */
	public ItemElement.ControlType getType() {
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
		clickGenerator.accept(parent);
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
			if (getParent().isPaginated()) {
				return ((InventoryElement.Paginated) getParent()).getPage(1);
			}
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
	 * @param player  the player
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
		BUTTON_BACK(ControlType::reloadInv),

		/**
		 * Symbolizes an entry point forward.
		 */
		BUTTON_NEXT(ControlType::reloadInv),

		/**
		 * Symbolizes an entry point exit.
		 */
		BUTTON_EXIT(i -> ControlType::close),

		/**
		 * Symbolizes a normal display item.
		 */
		DISPLAY(i -> ControlType::cancelClicks),

		/**
		 * Symbolizes an item that can be taken out of the inventory
		 */
		TAKEAWAY(i -> null),

		/**
		 * Symbolizes a {@link BorderElement} item.
		 */
		ITEM_BORDER(i -> ControlType::cancelClicks),

		/**
		 * Symbolizes a {} item.
		 */
		ITEM_FILLER(i -> ControlType::cancelClicks),

		/**
		 * Symbolizes a control button with special capabilities beyond the default implementations
		 */
		CUSTOM(i -> null);

		private final Function<InventoryElement, Menu.Click> clickHandlerGenerator;

		ControlType(final Function<InventoryElement, Menu.Click> clickHandlerGenerator) {
			this.clickHandlerGenerator = clickHandlerGenerator;
		}

		public void generateAndSetClick(InventoryElement inventoryElement, ItemElement<?> itemElement) {
			itemElement.click = clickHandlerGenerator.apply(inventoryElement);
		}

		public void generateAndSetClick(InventoryElement inventoryElement, ListElement<?> listElement) {
			listElement.getAttachment().forEach(i -> i.setType(this));
		}

		private static void cancelClicks(ClickElement clickElement) {
			clickElement.setHotbarAllowed(false);
			clickElement.setCancelled(true);
		}

		private static Menu.Click reloadInv(InventoryElement inventoryElement) {
			return c -> {
				cancelClicks(c);
				c.setConsumer((p, s) -> {
					if (s) {
						inventoryElement.open(p);
					}
				});
			};
		}

		private static void close(ClickElement clickElement) {
			clickElement.getElement().closeInventory();
		}

		public static Menu.Click combine(InventoryElement inventoryElement, ControlType... types) {
			return c -> {
				for (ControlType type : types) {
					type.clickHandlerGenerator.apply(inventoryElement).apply(c);
				}
			};
		}

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
