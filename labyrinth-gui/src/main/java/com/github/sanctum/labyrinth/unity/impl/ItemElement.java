package com.github.sanctum.labyrinth.unity.impl;

import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.unity.construct.Menu;
import java.util.Optional;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemElement<V> extends Menu.Element<ItemStack, Menu.Click> {

	private boolean slotted;

	private Navigation navigation;

	private int slot;

	private V data;

	private ItemStack item;

	private InventoryElement.Page page;

	private Menu.Click click;

	private InventoryElement parent;

	public ItemElement() {
	}

	public ItemElement(V data) {
		this.data = data;
	}

	public Optional<V> getData() {
		return Optional.ofNullable(this.data);
	}

	public Optional<Integer> getSlot() {
		return slotted ? Optional.of(this.slot) : Optional.empty();
	}

	public ItemElement<V> setSlot(int slot) {
		this.slot = slot;
		this.slotted = true;
		return this;
	}

	public ItemElement<V> setClick(Menu.Click click) {
		this.click = click;
		return this;
	}

	public ItemElement<V> setElement(ItemStack item) {
		this.item = item;
		return this;
	}

	public ItemElement<V> setElement(Function<Item.Edit, ItemStack> edit) {
		Item.Edit ed;
		if (this.item != null) {
			ed = Items.edit(this.item);
		} else {
			ed = Items.edit();
		}
		this.item = edit.apply(ed);
		return this;
	}

	public ItemElement<V> setNavigation(Navigation navigation) {
		this.navigation = navigation;
		return this;
	}

	public @Nullable Navigation getNavigation() {
		return this.navigation;
	}

	@Override
	public @NotNull ItemStack getElement() {
		return this.item;
	}

	public ItemElement<?> setParent(InventoryElement parent) {
		this.parent = parent;
		return this;
	}

	public InventoryElement getParent() {
		return parent;
	}

	@Override
	public Menu.Click getAttachment() {
		return this.click;
	}

	public ItemElement<?> setPage(@NotNull InventoryElement.Page page) {
		this.page = page;
		return this;
	}

	public InventoryElement.Page getPage() {
		if (this.page == null) {
			boolean tooFull = true;
			for (InventoryElement.Page p : getParent().getAllPages()) {
				if (!p.isFull()) {
					tooFull = false;
					setPage(p);
					break;
				}
			}

			if (tooFull) {
				setPage(getParent().getPage(getParent().getAllPages().size()));
			}
		}
		return this.page;
	}

	public String getName() {
		return getElement().getItemMeta() != null ? getElement().getItemMeta().getDisplayName() : getElement().getType().name();
	}

	public enum Navigation {
		Previous,
		Next,
		Back
	}

}
